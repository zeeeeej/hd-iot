#ifndef H__HD_MD5__H
#define H__HD_MD5__H

#include <stdint.h>
#include <string.h>

#ifdef __cplusplus
extern "C" {
#endif

#include <stdio.h>
#include <stdlib.h>
#include <pthread.h>
#include <unistd.h>
#include <time.h>
#include <sys/time.h>

int hd_md5_data( unsigned char  *data,size_t size,uint8_t *result);
int hd_md5_file(const char *file, uint8_t *result);
int hd_md5(const char *file_path, unsigned char result[16]); // +++

#ifdef __cplusplus
}
#endif

#endif // H__HD_MD5__H
