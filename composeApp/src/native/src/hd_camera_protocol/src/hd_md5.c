#include <stdio.h>
#include <stdlib.h>
#include <time.h>
#include <dirent.h>
#include <string.h>
#include <stdint.h>
#include <hd_md5.h>

// 左循环移位函数
#define LEFTROTATE(x, c) (((x) << (c)) | ((x) >> (32 - (c))))

// MD5算法的四个基本函数
#define F(x, y, z) (((x) & (y)) | ((~x) & (z)))
#define G(x, y, z) (((x) & (z)) | ((y) & (~z)))
#define H(x, y, z) ((x) ^ (y) ^ (z))
#define I(x, y, z) ((y) ^ ((x) | (~z)))

// MD5的每一轮操作
#define MD5_ROUND1(a, b, c, d, x, s, ac) { \
    (a) += F((b), (c), (d)) + (x) + (uint32_t)(ac); \
    (a) = LEFTROTATE((a), (s)); \
    (a) += (b); \
}

#define MD5_ROUND2(a, b, c, d, x, s, ac) { \
    (a) += G((b), (c), (d)) + (x) + (uint32_t)(ac); \
    (a) = LEFTROTATE((a), (s)); \
    (a) += (b); \
}

#define MD5_ROUND3(a, b, c, d, x, s, ac) { \
    (a) += H((b), (c), (d)) + (x) + (uint32_t)(ac); \
    (a) = LEFTROTATE((a), (s)); \
    (a) += (b); \
}

#define MD5_ROUND4(a, b, c, d, x, s, ac) { \
    (a) += I((b), (c), (d)) + (x) + (uint32_t)(ac); \
    (a) = LEFTROTATE((a), (s)); \
    (a) += (b); \
}

// MD5上下文结构
typedef struct {
    uint32_t state[4];    // 状态 (ABCD)
    uint32_t count[2];    // 位数计数器，低32位在前
    uint8_t buffer[64];   // 输入缓冲区
} HD_MD5_CTX;

// MD5填充字节
static const uint8_t PADDING[64] = {
        0x80, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0,
        0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
};

// 辅助函数：将字节数组编码为32位整数数组
static void Encode(uint8_t *output, const uint32_t *input, size_t len) {
    size_t i, j;

    for (i = 0, j = 0; j < len; i++, j += 4) {
        output[j] = (uint8_t) (input[i] & 0xff);
        output[j + 1] = (uint8_t) ((input[i] >> 8) & 0xff);
        output[j + 2] = (uint8_t) ((input[i] >> 16) & 0xff);
        output[j + 3] = (uint8_t) ((input[i] >> 24) & 0xff);
    }
}

// 辅助函数：将字节数组解码为32位整数数组
static void Decode(uint32_t *output, const uint8_t *input, size_t len) {
    size_t i, j;

    for (i = 0, j = 0; j < len; i++, j += 4)
        output[i] = ((uint32_t) input[j]) | (((uint32_t) input[j + 1]) << 8) |
                    (((uint32_t) input[j + 2]) << 16) | (((uint32_t) input[j + 3]) << 24);
}

// MD5核心变换
static void MD5_Transform(uint32_t state[4], const uint8_t block[64]) {
    uint32_t a = state[0], b = state[1], c = state[2], d = state[3], x[16];

    Decode(x, block, 64);

    // 第1轮
    MD5_ROUND1(a, b, c, d, x[0], 7, 0xd76aa478);
    MD5_ROUND1(d, a, b, c, x[1], 12, 0xe8c7b756);
    MD5_ROUND1(c, d, a, b, x[2], 17, 0x242070db);
    MD5_ROUND1(b, c, d, a, x[3], 22, 0xc1bdceee);
    MD5_ROUND1(a, b, c, d, x[4], 7, 0xf57c0faf);
    MD5_ROUND1(d, a, b, c, x[5], 12, 0x4787c62a);
    MD5_ROUND1(c, d, a, b, x[6], 17, 0xa8304613);
    MD5_ROUND1(b, c, d, a, x[7], 22, 0xfd469501);
    MD5_ROUND1(a, b, c, d, x[8], 7, 0x698098d8);
    MD5_ROUND1(d, a, b, c, x[9], 12, 0x8b44f7af);
    MD5_ROUND1(c, d, a, b, x[10], 17, 0xffff5bb1);
    MD5_ROUND1(b, c, d, a, x[11], 22, 0x895cd7be);
    MD5_ROUND1(a, b, c, d, x[12], 7, 0x6b901122);
    MD5_ROUND1(d, a, b, c, x[13], 12, 0xfd987193);
    MD5_ROUND1(c, d, a, b, x[14], 17, 0xa679438e);
    MD5_ROUND1(b, c, d, a, x[15], 22, 0x49b40821);

    // 第2轮
    MD5_ROUND2(a, b, c, d, x[1], 5, 0xf61e2562);
    MD5_ROUND2(d, a, b, c, x[6], 9, 0xc040b340);
    MD5_ROUND2(c, d, a, b, x[11], 14, 0x265e5a51);
    MD5_ROUND2(b, c, d, a, x[0], 20, 0xe9b6c7aa);
    MD5_ROUND2(a, b, c, d, x[5], 5, 0xd62f105d);
    MD5_ROUND2(d, a, b, c, x[10], 9, 0x02441453);
    MD5_ROUND2(c, d, a, b, x[15], 14, 0xd8a1e681);
    MD5_ROUND2(b, c, d, a, x[4], 20, 0xe7d3fbc8);
    MD5_ROUND2(a, b, c, d, x[9], 5, 0x21e1cde6);
    MD5_ROUND2(d, a, b, c, x[14], 9, 0xc33707d6);
    MD5_ROUND2(c, d, a, b, x[3], 14, 0xf4d50d87);
    MD5_ROUND2(b, c, d, a, x[8], 20, 0x455a14ed);
    MD5_ROUND2(a, b, c, d, x[13], 5, 0xa9e3e905);
    MD5_ROUND2(d, a, b, c, x[2], 9, 0xfcefa3f8);
    MD5_ROUND2(c, d, a, b, x[7], 14, 0x676f02d9);
    MD5_ROUND2(b, c, d, a, x[12], 20, 0x8d2a4c8a);

    // 第3轮
    MD5_ROUND3(a, b, c, d, x[5], 4, 0xfffa3942);
    MD5_ROUND3(d, a, b, c, x[8], 11, 0x8771f681);
    MD5_ROUND3(c, d, a, b, x[11], 16, 0x6d9d6122);
    MD5_ROUND3(b, c, d, a, x[14], 23, 0xfde5380c);
    MD5_ROUND3(a, b, c, d, x[1], 4, 0xa4beea44);
    MD5_ROUND3(d, a, b, c, x[4], 11, 0x4bdecfa9);
    MD5_ROUND3(c, d, a, b, x[7], 16, 0xf6bb4b60);
    MD5_ROUND3(b, c, d, a, x[10], 23, 0xbebfbc70);
    MD5_ROUND3(a, b, c, d, x[13], 4, 0x289b7ec6);
    MD5_ROUND3(d, a, b, c, x[0], 11, 0xeaa127fa);
    MD5_ROUND3(c, d, a, b, x[3], 16, 0xd4ef3085);
    MD5_ROUND3(b, c, d, a, x[6], 23, 0x04881d05);
    MD5_ROUND3(a, b, c, d, x[9], 4, 0xd9d4d039);
    MD5_ROUND3(d, a, b, c, x[12], 11, 0xe6db99e5);
    MD5_ROUND3(c, d, a, b, x[15], 16, 0x1fa27cf8);
    MD5_ROUND3(b, c, d, a, x[2], 23, 0xc4ac5665);

    // 第4轮
    MD5_ROUND4(a, b, c, d, x[0], 6, 0xf4292244);
    MD5_ROUND4(d, a, b, c, x[7], 10, 0x432aff97);
    MD5_ROUND4(c, d, a, b, x[14], 15, 0xab9423a7);
    MD5_ROUND4(b, c, d, a, x[5], 21, 0xfc93a039);
    MD5_ROUND4(a, b, c, d, x[12], 6, 0x655b59c3);
    MD5_ROUND4(d, a, b, c, x[3], 10, 0x8f0ccc92);
    MD5_ROUND4(c, d, a, b, x[10], 15, 0xffeff47d);
    MD5_ROUND4(b, c, d, a, x[1], 21, 0x85845dd1);
    MD5_ROUND4(a, b, c, d, x[8], 6, 0x6fa87e4f);
    MD5_ROUND4(d, a, b, c, x[15], 10, 0xfe2ce6e0);
    MD5_ROUND4(c, d, a, b, x[6], 15, 0xa3014314);
    MD5_ROUND4(b, c, d, a, x[13], 21, 0x4e0811a1);
    MD5_ROUND4(a, b, c, d, x[4], 6, 0xf7537e82);
    MD5_ROUND4(d, a, b, c, x[11], 10, 0xbd3af235);
    MD5_ROUND4(c, d, a, b, x[2], 15, 0x2ad7d2bb);
    MD5_ROUND4(b, c, d, a, x[9], 21, 0xeb86d391);

    state[0] += a;
    state[1] += b;
    state[2] += c;
    state[3] += d;

    // 清零敏感信息
    memset(x, 0, sizeof(x));
}

// 初始化MD5上下文
void hd_MD5_Init(HD_MD5_CTX *context) {
    context->count[0] = context->count[1] = 0;

    // 初始化状态
    context->state[0] = 0x67452301;
    context->state[1] = 0xEFCDAB89;
    context->state[2] = 0x98BADCFE;
    context->state[3] = 0x10325476;
}

// 对输入数据进行MD5处理
void hd_MD5_Update(HD_MD5_CTX *context, const uint8_t *input, size_t inputLen) {
    size_t i, index, partLen;

    // 计算当前buffer中有多少字节
    index = (size_t) ((context->count[0] >> 3) & 0x3F);

    // 更新位数计数器
    if ((context->count[0] += ((uint32_t) inputLen << 3)) < ((uint32_t) inputLen << 3))
        context->count[1]++;
    context->count[1] += ((uint32_t) inputLen >> 29);

    partLen = 64 - index;

    // 如果输入数据足够填满buffer
    if (inputLen >= partLen) {
        memcpy(&context->buffer[index], input, partLen);
        MD5_Transform(context->state, context->buffer);

        for (i = partLen; i + 63 < inputLen; i += 64)
            MD5_Transform(context->state, &input[i]);

        index = 0;
    } else {
        i = 0;
    }

    // 将剩余数据存入buffer
    memcpy(&context->buffer[index], &input[i], inputLen - i);
}

// 完成MD5计算，输出结果
void hd_MD5_Final(HD_MD5_CTX *context, uint8_t digest[16]) {
    uint8_t bits[8];
    size_t index, padLen;

    // 保存位数
    Encode(bits, context->count, 8);

    // 填充到448位（模512）
    index = (size_t) ((context->count[0] >> 3) & 0x3f);
    padLen = (index < 56) ? (56 - index) : (120 - index);
    hd_MD5_Update(context, PADDING, padLen);

    // 附加长度
    hd_MD5_Update(context, bits, 8);

    // 存储状态到digest
    Encode(digest, context->state, 16);

    // 清零敏感信息
    memset(context, 0, sizeof(*context));
}

/**
 * 使用系统调用system()生成文件的md5
 */
static int hd_md5_str(const char *file_path, unsigned char result[16]) {
    if (file_path == NULL)return -1;
    size_t size = strlen(file_path);
    printf("size = %zu\n", size);
    if (size == 0)return -1;
    unsigned char buff[1024];
    for (size_t i = 0; i < size; ++i) {
        buff[i] = file_path[i];
    }
    HD_MD5_CTX context;
    hd_MD5_Init(&context);
    hd_MD5_Update(&context, buff, size);
    hd_MD5_Final(&context, result);
    return 0;
}

int hd_md5_data(unsigned char *data, size_t size, uint8_t *result) {
    HD_MD5_CTX ctx;
    hd_MD5_Init(&ctx);
    hd_MD5_Update(&ctx, data, size);
    hd_MD5_Final(&ctx, result);
    return 0;
}

int hd_md5_file(const char *file_name, uint8_t *result) {
    FILE *file = NULL;
    if ((file = fopen(file_name, "rb")) == NULL) {
        perror("fopen");
        return -1;
    }
    unsigned char *input_buffer = malloc(1024);
    size_t input_size = 0;

    HD_MD5_CTX ctx;
    hd_MD5_Init(&ctx);

    while ((input_size = fread(input_buffer, 1, 1024, file)) > 0) {
        hd_MD5_Update(&ctx, (uint8_t *) input_buffer, input_size);
    }

    hd_MD5_Final(&ctx, result);
    return 0;
}

int hd_md5(const char *file_path, unsigned char result[16]) {
    if (file_path == NULL || result == NULL) {
        return -1; // 参数错误
    }

    // 构造命令字符串
    char command[256];
    snprintf(command, sizeof(command), "md5sum %s", file_path);

    // 执行命令并获取输出
    FILE *pipe = popen(command, "r");
    if (pipe == NULL) {
        return -2; // 命令执行失败
    }

    // 读取命令输出
    char output[64];
    if (fgets(output, sizeof(output), pipe) == NULL) {
        pclose(pipe);
        return -3; // 读取输出失败
    }

    pclose(pipe);

    // 解析 MD5 哈希值
    if (sscanf(output, "%32s", output) != 1) {
        return -4; // 解析失败
    }

    // 将十六进制字符串转换为字节数组
    for (int i = 0; i < 16; i++) {
        char hex[3] = {output[2 * i], output[2 * i + 1], '\0'};
        result[i] = (unsigned char) strtol(hex, NULL, 16);
    }

    return 0; // 成功
}



