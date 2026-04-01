#include "prewhiten.h"
#include "object_tracking/utils.h"

#ifdef __ARM_NEON
#include <arm_neon.h>
#endif

void Prewhiten(const float* const input, const int num_vals, float* const output) {
    float mean = tf_tracking::ComputeMean(input, num_vals);
    float std = tf_tracking::ComputeStdDev(input, num_vals, mean);
    auto std_adj = (float) fmax(std, 1.0/sqrt(num_vals));

    Normalize(input, mean, std_adj, num_vals, output);
}

#ifdef __ARM_NEON
void NormalizeNeon(const float* const input, const float mean,
        const float std_adj, const int num_vals, float* const output) {
    const float32x4_t mean_vec = vdupq_n_f32(-mean);
    const float32x4_t std_vec = vdupq_n_f32(1/std_adj);

    float32x4_t result;

    int offset = 0;
    for (; offset <= num_vals - 4; offset += 4) {
        const float32x4_t deltas =
                vaddq_f32(mean_vec, vld1q_f32(&input[offset]));

        result = vmulq_f32(deltas, std_vec);
        vst1q_f32(&output[offset], result);
    }

    // Get the remaining 1 to 3 values.
    for (; offset < num_vals; ++offset) {
        output[offset] = (input[offset] - mean) / std_adj;
    }
}
#endif