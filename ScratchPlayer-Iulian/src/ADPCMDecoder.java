import java.io.ByteArrayInputStream;
import java.io.DataInputStream;
import java.io.InputStream;
import java.io.IOException;

class ADPCMDecoder {
	// decoder tables
	static final int[] stepSizeTable = {
		7, 8, 9, 10, 11, 12, 13, 14, 16, 17, 19, 21, 23, 25, 28, 31, 34, 37, 41, 45,
		50, 55, 60, 66, 73, 80, 88, 97, 107, 118, 130, 143, 157, 173, 190, 209, 230,
		253, 279, 307, 337, 371, 408, 449, 494, 544, 598, 658, 724, 796, 876, 963,
		1060, 1166, 1282, 1411, 1552, 1707, 1878, 2066, 2272, 2499, 2749, 3024, 3327,
		3660, 4026, 4428, 4871, 5358, 5894, 6484, 7132, 7845, 8630, 9493, 10442, 11487,
		12635, 13899, 15289, 16818, 18500, 20350, 22385, 24623, 27086, 29794, 32767};
	static final int[] indices2bit = {-1, 2};
	static final int[] indices3bit = {-1, -1, 2, 4};
	static final int[] indices4bit = {-1, -1, -1, -1, 2, 4, 6, 8};
	static final int[] indices5bit = {-1, -1, -1, -1, -1, -1, -1, -1, 1, 2, 4, 6, 8, 10, 13, 16};

	// bit stream state
	DataInputStream in;
	int bitsPerSample;
	int currentByte;
	int bitPosition;

	// decoder state
	int[] indexTable;
	int predicted;
	int index;

	ADPCMDecoder(byte[] buf, int bits) {
		this(new ByteArrayInputStream(buf), bits);
	}

	ADPCMDecoder(InputStream s, int bits) {
		in = new DataInputStream(s);
		bitsPerSample = bits;
		switch(bitsPerSample) {
		case 2:
			indexTable = indices2bit;
			break;
		case 3:
			indexTable = indices3bit;
			break;
		case 4:
			indexTable = indices4bit;
			break;
		case 5:
			indexTable = indices5bit;
			break;
		}
	}

	int[] decode(int sampleCount) {
		int deltaSignMask = 1 << (bitsPerSample - 1);
		int deltaValueMask = deltaSignMask - 1;
		int deltaValueHighBit = deltaSignMask >> 1;
		int delta, step, predictedDelta, bit;

		int[] result = new int[sampleCount];

		for (int i = 0; i < sampleCount; i++) {
			delta = nextBits();
			step = stepSizeTable[index];
			predictedDelta = 0;
			for (bit = deltaValueHighBit; bit > 0; bit = bit >> 1) {
				if ((delta & bit) != 0) predictedDelta += step;
				step = step >> 1;
			}
			predictedDelta += step;

			predicted += ((delta & deltaSignMask) != 0) ? -predictedDelta : predictedDelta;
			if (predicted > 32767) predicted = 32767;
			if (predicted < -32768) predicted = -32768;
			result[i] = predicted;

			index += indexTable[delta & deltaValueMask];
			if (index < 0) index = 0;
			if (index > 88) index = 88;
		}
		return result;
	}

	int nextBits() {
		int result = 0;
		int remaining = bitsPerSample;
		while (true) {
			int shift = remaining - bitPosition;
			result += (shift < 0) ? (currentByte >> -shift) : (currentByte << shift);
			if (shift > 0) {  // consumed all bits of currentByte; fetch next byte
				remaining -= bitPosition;
				try {
					currentByte = in.readUnsignedByte();
				} catch (IOException e) {
					currentByte = -1;
				}
				bitPosition = 8;
				if (currentByte < 0) {  // no more input
					bitPosition = 0;
					return result;
				}
			} else {  // still some bits left in currentByte
				bitPosition -= remaining;
				currentByte = currentByte & (0xFF >> (8 - bitPosition));
				return result;
			}
		}
	}
}
