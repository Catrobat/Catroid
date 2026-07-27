#ifndef ORG_TENSORFLOW_JNI_IMAGEUTILS_PREWHITEN_H_
#define ORG_TENSORFLOW_JNI_IMAGEUTILS_PREWHITEN_H_

#include <math.h>
#include <stdint.h>

#ifdef __cplusplus
extern "C" {
#endif

void Prewhiten(const float* const input, const int num_vals, float* const output);

#ifdef __ARM_NEON
void NormalizeNeon(const float* const input, const float mean,
        const float std_adj, const int num_vals, float* const output);
#endif

inline void NormalizeCpu(const float* const input, const float mean,
        const float std_adj, const int num_vals, float* const output) {
    for (int i = 0; i < num_vals; ++i) {
        output[i] = (input[i] - mean) / std_adj;
    }
}

inline void Normalize(const float* const input, const float mean,
        const float std_adj, const int num_vals, float* const output) {
#ifdef __ARM_NEON
    (num_vals >= 8) ? NormalizeNeon(input, mean, std_adj, num_vals, output)
                    :
#endif
    NormalizeCpu(input, mean, std_adj, num_vals, output);
}

#ifdef __cplusplus
}
#endif

#endif  // ORG_TENSORFLOW_JNI_IMAGEUTILS_PREWHITEN_H_
