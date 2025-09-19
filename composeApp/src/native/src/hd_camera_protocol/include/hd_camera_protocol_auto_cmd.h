
#ifndef H__HD_CAMERA_PROTOCOL_AUTO_CMD__H
#define H__HD_CAMERA_PROTOCOL_AUTO_CMD__H

#include <stdint.h>
#include <string.h>
#include "hd_camera_protocol_cmd.h"

#ifdef __cplusplus
extern "C" {
#endif

#define CMD_HD_AUTO_ALLOC_ADDRESS          0x11     // 分配从机广播
#define CMD_HD_AUTO_ALLOC_ADDRESS_CONFIRM  0x12     // 确认分配从机广播
#define CMD_HD_AUTO_CHECK_ADDRESS          0x13     // 查询从机自检广播
#define CMD_HD_AUTO_CHECK_ADDRESS_CONFIRM  0x14     // 确认从机自检广播
#define CMD_HD_AUTO_CHECK_START            0xDE     /**废弃**/// 通知从机开启自检程序

#define CMD_HD_AUTO_PIC_SNAP                    0x16     // 主动抓图
#define CMD_HD_AUTO_PIC_FIND                    0x17     // 查询图片
#define CMD_HD_AUTO_PIC_DELETE                  0x18     // 删除图片
#define CMD_HD_AUTO_PIC_PULL                    0x19     // 拉取图片
#define CMD_HD_AUTO_PIC_PULL_COMPLETED          0x1A     // 拉取图片完成

#define CMD_HD_AUTO_RESP_RANDOM_INTERNAL        1500000  // 随机时间1.5秒

/* ******** encode *********/
/* ******** encode *********/
/* ******** encode *********/
/**
 *  随机分配从机广播(0x11)
 */
uint8_t hd_host_auto_alloc_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t alloc_addr
);

/**
 *  主机确认分配从机广播(0x12)
 */
uint8_t hd_host_auto_alloc_address_confirm_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t alloc_addr,
        uint32_t key
);


/**
 * 从机应答随机分配从机广播(0x12)
 */
uint8_t hd_slave_auto_alloc_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t alloc_addr,
        uint32_t key
);

/**
 * 查询从机是否确认从机地址(0x13)
 */
uint8_t hd_host_auto_check_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
);

/**
  * 主机广播确认从机地址(0x14)
 */
uint8_t hd_host_auto_check_address_encode_confirm(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t direction
);

/**
 * 从机应答查询从机是否确认从机地址(0xDC)
 */
uint8_t hd_slave_auto_check_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result
);

/**
 * 废弃
 */
uint8_t hd_host_auto_self_check_start_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t start
);

/**
 * 主动抓图
 */
uint8_t hd_host_auto_pic_snap_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
);

uint8_t hd_slave_auto_pic_snap_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result,
        uint8_t pic_id
);

/**
 * 查找图片
 */
uint8_t hd_host_auto_pic_find_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
);

uint8_t hd_slave_auto_pic_find_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        hd_dynamic_pic_info *infos,
        uint32_t info_size
);

/**
 * 删除图片
 */
uint8_t hd_host_auto_pic_delete_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t pic_id
);
uint8_t hd_slave_auto_pic_delete_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result
);

/**
 * 拉取图片
 */
uint8_t hd_host_auto_pic_pull_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t pic_id,
        uint32_t offset,
        uint32_t len
);

uint8_t hd_slave_auto_pic_pull_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result,uint8_t * data,uint32_t data_size
);

/**
 * 拉取图片完成
 */
uint8_t hd_host_auto_pic_pull_completed_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
);

uint8_t hd_slave_auto_pic_pull_completed_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result
);

/* ******** decode *********/
/* ******** decode *********/
/* ******** decode *********/
/**
 * 对应hd_host_auto_alloc_address_encode
 */
uint8_t hd_slave_auto_alloc_address_decode(
        uint8_t *alloc_addr,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 hd_host_auto_alloc_address_confirm_encode
 */
uint8_t hd_slave_auto_alloc_address_confirm_decode(
        uint8_t *alloc_addr,
        uint32_t *key,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 hd_slave_auto_alloc_address_encode
 */
uint8_t hd_host_auto_alloc_address_decode(
        uint8_t *alloc_addr,
        uint32_t *key,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 hd_host_auto_check_address_encode
 */
uint8_t hd_slave_auto_check_address_decode(
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 hd_host_auto_check_address_encode_confirm
 */
uint8_t hd_slave_auto_check_address_confirm_decode(
        uint8_t *direction,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 hd_slave_auto_check_address_encode
 */
uint8_t hd_host_auto_check_address_decode(
        uint8_t *result,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 废弃
 * 对应 hd_host_auto_self_check_start
 */
uint8_t hd_slave_auto_self_check_start_decode(
        uint8_t *start,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 抓图 hd_host_auto_pic_snap_encode
 */
uint8_t hd_slave_auto_pic_snap_decode(
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 抓图 hd_slave_auto_pic_snap_encode
 */
uint8_t hd_host_auto_pic_snap_decode(
        uint8_t *result,
        uint8_t *pic_id,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 查找图片 hd_host_auto_pic_find_encode
 */

uint8_t hd_slave_auto_pic_find_decode(
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 查找图片 hd_slave_auto_pic_find_encode
 */
uint8_t hd_host_auto_pic_find_decode(
        hd_dynamic_pic_info *infos,
        uint32_t *info_size,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 删除图片 hd_host_auto_pic_delete_encode
 */

uint8_t hd_slave_auto_pic_delete_decode(
        uint8_t *pic_id,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 删除图片 hd_slave_auto_pic_delete_encode
 */
uint8_t hd_host_auto_pic_delete_decode(
        uint8_t *result,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 拉取图片 hd_host_auto_pic_pull_encode
 */

uint8_t hd_slave_auto_pic_pull_decode(
        uint8_t *pic_id,
        uint32_t *offset,
        uint32_t *len,
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 拉取图片 hd_slave_auto_pic_pull_encode
 */
uint8_t hd_host_auto_pic_pull_decode(
        uint8_t *result,uint8_t * data,uint32_t *data_size,
        const unsigned char *payload,
        uint32_t payload_size
);


/**
 * 对应 拉取图片完成 hd_host_auto_pic_pull_completed_encode
 */

uint8_t hd_slave_auto_pic_pull_completed_decode(
        const unsigned char *payload,
        uint32_t payload_size
);

/**
 * 对应 拉取图片完成 hd_slave_auto_pic_pull_completed_encode
 */
uint8_t hd_host_auto_pic_pull_completed_decode(
        uint8_t *result,
        const unsigned char *payload,
        uint32_t payload_size
);


#ifdef __cplusplus
}
#endif

#endif // H__HD_CAMERA_PROTOCOL_AUTO_CMD__H
