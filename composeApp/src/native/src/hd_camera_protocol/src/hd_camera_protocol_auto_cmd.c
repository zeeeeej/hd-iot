#include <stdint.h>
#include <stddef.h>
#include <string.h>
#include <stdlib.h>
#include <stdio.h>
#include "hd_camera_protocol_auto_cmd.h"
#include "hd_camera_protocol.h"


static uint8_t hd_dynamic_pic_infos_encode_simple(
        hd_dynamic_pic_info *infos,
        uint32_t count,
        unsigned char **result,
        uint32_t *result_size
);


//<editor-fold desc="encode">
/* ******** encode *********/
/* ******** encode *********/
/* ******** encode *********/
/**
 *  随机分配从机广播
 */
uint8_t hd_host_auto_alloc_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t alloc_addr
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    payload[0] = alloc_addr;

    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_ALLOC_ADDRESS,
            1,
            payload
    );
    return ret;
}

/**
 *  主机确认分配从机广播
 */
uint8_t hd_host_auto_alloc_address_confirm_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t alloc_addr,
        uint32_t key
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[5] = {0};
    int pos = -1;
    payload[++pos] = alloc_addr;
    payload[++pos] = (key >> 0) & 0xFF;  // LSB
    payload[++pos] = (key >> 8) & 0xFF;
    payload[++pos] = (key >> 16) & 0xFF;
    payload[++pos] = (key >> 24) & 0xFF; // MSB
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_ALLOC_ADDRESS_CONFIRM,
            5,
            payload
    );
    return ret;
}


/**
 * 从机应答随机分配从机广播
 */
uint8_t hd_slave_auto_alloc_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t alloc_addr,
        uint32_t key
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[5] = {0};
    int pos = -1;
    payload[++pos] = alloc_addr;
    payload[++pos] = (key >> 0) & 0xFF;  // LSB
    payload[++pos] = (key >> 8) & 0xFF;
    payload[++pos] = (key >> 16) & 0xFF;
    payload[++pos] = (key >> 24) & 0xFF; // MSB
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_ALLOC_ADDRESS,
            5,
            payload
    );
    return ret;
}

/**
 * 查询从机是否确认从机地址
 */
uint8_t hd_host_auto_check_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_CHECK_ADDRESS,
            0,
            NULL
    );
    return ret;
}

/**
  * 主机广播确认从机地址
 */
uint8_t hd_host_auto_check_address_encode_confirm(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t direction
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    int pos = -1;
    payload[++pos] = direction;
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_CHECK_ADDRESS_CONFIRM,
            1,
            payload
    );
    return ret;
}

/**
 * 从机应答查询从机是否确认从机地址
 */
uint8_t hd_slave_auto_check_address_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    int pos = -1;
    payload[++pos] = result;
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_CHECK_ADDRESS,
            1,
            payload
    );
    return ret;
}

uint8_t hd_host_auto_self_check_start_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t start
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    payload[0] = start;

    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_ALLOC_ADDRESS,
            1,
            payload
    );
    return ret;
}


/**
 * 主动抓图
 */
uint8_t hd_host_auto_pic_snap_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }

    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_SNAP,
            0,
            NULL
    );
    return ret;
}

uint8_t hd_slave_auto_pic_snap_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result,
        uint8_t pic_id
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[2] = {0};
    payload[0] = result;
    payload[1] = pic_id;
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_SNAP,
            2,
            payload
    );
    return ret;
}

/**
 * 查找图片
 */
uint8_t hd_host_auto_pic_find_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }

    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_FIND,
            0,
            NULL
    );
    return ret;
}

uint8_t hd_slave_auto_pic_find_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        hd_dynamic_pic_info *infos,
        uint32_t info_size
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    uint8_t *payload;
    uint32_t payload_size;
    int ret;
    ret = hd_dynamic_pic_infos_encode_simple(infos, info_size, &payload, &payload_size);
    if (ret || payload == NULL) {
        return 2;
    }
    ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_FIND,
            payload_size,
            payload
    );
    free(payload);
    payload = NULL;
    return ret;
}

/**
 * 删除图片
 */
uint8_t hd_host_auto_pic_delete_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t pic_id

) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    payload[0] = pic_id;
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_DELETE,
            1,
            payload
    );
    return ret;
}

uint8_t hd_slave_auto_pic_delete_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    payload[0] = result;
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_DELETE,
            1,
            payload
    );
    return ret;
}

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
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[9] = {0};
    int pos = -1;
    payload[++pos] = pic_id;
    payload[++pos] = (offset >> 0) & 0xFF;  // LSB
    payload[++pos] = (offset >> 8) & 0xFF;
    payload[++pos] = (offset >> 16) & 0xFF;
    payload[++pos] = (offset >> 24) & 0xFF; // MSB
    payload[++pos] = (len >> 0) & 0xFF;  // LSB
    payload[++pos] = (len >> 8) & 0xFF;
    payload[++pos] = (len >> 16) & 0xFF;
    payload[++pos] = (len >> 24) & 0xFF; // MSB

    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_PULL,
            9,
            payload
    );
    return ret;
}

uint8_t hd_slave_auto_pic_pull_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result, uint8_t *data, uint32_t data_size
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    uint32_t payload_size = 1 + data_size;
    unsigned char *payload = (unsigned char *) malloc(payload_size);
    payload[0] = result;
    memcpy(&payload[1], data, data_size);
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_PULL,
            payload_size,
            payload
    );
    return ret;
}

/**
 * 拉取图片完成
 */
uint8_t hd_host_auto_pic_pull_completed_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_PULL_COMPLETED,
            0,
            NULL
    );
    return ret;
}

uint8_t hd_slave_auto_pic_pull_completed_encode(
        unsigned char **out_protocol_data,
        uint32_t *out_protocol_data_size,
        uint8_t addr,
        uint8_t result
) {
    if (out_protocol_data == NULL || out_protocol_data_size == NULL) {
        return 1;
    }
    unsigned char payload[1] = {0};
    payload[0] = result;
    int ret = hd_camera_protocol_encode(
            out_protocol_data,
            out_protocol_data_size,
            addr,
            CMD_HD_AUTO_PIC_PULL_COMPLETED,
            1,
            payload
    );
    return ret;
}
//</editor-fold>


//<editor-fold desc="decode">
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
) {
    if (alloc_addr == NULL || payload == NULL) {
        return 2;
    }
    if (payload_size != 1) {
        return 3;
    }

    *alloc_addr = payload[0];
    return 0;
}

/**
 * 对应 hd_host_auto_alloc_address_confirm_encode
 */
uint8_t hd_slave_auto_alloc_address_confirm_decode(
        uint8_t *alloc_addr,
        uint32_t *key,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (alloc_addr == NULL || payload == NULL || key == NULL) {
        return 2;
    }
    if (payload_size != 5) {
        return 3;
    }

    *alloc_addr = payload[0];
    *key = (uint32_t) payload[1] |
           (uint32_t) payload[2] << 8 |
           (uint32_t) payload[3] << 16 |
           (uint32_t) payload[4] << 24;
    return 0;
}

/**
 * 对应 hd_slave_auto_alloc_address_encode
 */
uint8_t hd_host_auto_alloc_address_decode(
        uint8_t *alloc_addr,
        uint32_t *key,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (alloc_addr == NULL || payload == NULL || key == NULL) {
        return 2;
    }
    if (payload_size != 6) {
        return 3;
    }

    *alloc_addr = payload[0];
    *key = (uint32_t) payload[1] |
           (uint32_t) payload[2] << 8 |
           (uint32_t) payload[3] << 16 |
           (uint32_t) payload[4] << 24;
    return 0;
}

/**
 * 对应 hd_host_auto_check_address_encode
 */
uint8_t hd_slave_auto_check_address_decode(
        const unsigned char *payload,
        uint32_t payload_size
) {
    return 0;
}

/**
 * 对应 hd_host_auto_check_address_encode_confirm
 */
uint8_t hd_slave_auto_check_address_confirm_decode(
        uint8_t *direction,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (direction == NULL) {
        return 2;
    }
    if (payload_size != 1) {
        return 3;
    }

    *direction = payload[0];
    return 0;
}

/**
 * 对应 hd_slave_auto_check_address_encode
 */
uint8_t hd_host_auto_check_address_decode(
        uint8_t *result,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (result == NULL) {
        return 2;
    }
    if (payload_size != 1) {
        return 3;
    }

    *result = payload[0];
    return 0;
}

uint8_t hd_slave_auto_self_check_start_decode(
        uint8_t *start,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (start == NULL) {
        return 2;
    }
    if (payload_size != 1) {
        return 3;
    }

    *start = payload[0];
    return 0;
}

/**
 * 对应 抓图 hd_host_auto_pic_snap_encode
 */
uint8_t hd_slave_auto_pic_snap_decode(
        const unsigned char *payload,
        uint32_t payload_size
) {
    return 0;
}

/**
 * 对应 抓图 hd_slave_auto_pic_snap_encode
 */
uint8_t hd_host_auto_pic_snap_decode(
        uint8_t *result,
        uint8_t *pic_id,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (result == NULL || pic_id == NULL) {
        return 2;
    }
    if (payload == NULL || payload_size != 2) {
        return 3;
    }
    *result = payload[0];
    *pic_id = payload[1];
    return 0;
}

/**
 * 对应 查找图片 hd_host_auto_pic_find_encode
 */

uint8_t hd_slave_auto_pic_find_decode(
        const unsigned char *payload,
        uint32_t payload_size
) {

    return 0;
}

/**
 * 对应 查找图片 hd_slave_auto_pic_find_encode
 */
uint8_t hd_host_auto_pic_find_decode(
        hd_dynamic_pic_info *infos,
        uint32_t *info_size,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (infos == NULL || info_size == NULL) {
        return 2;
    }
    if (payload == NULL || payload_size == 0) {
        return 3;
    }
    uint32_t pic_number = payload[0];
    if (payload_size - pic_number * 27 != 1) {
        perror("len != pic_number");
        return -2;
    }
    int pos = 1;

    for (uint32_t i = 0; i < pic_number; ++i) {
        hd_dynamic_pic_info info;
        uint8_t id = (uint16_t) payload[pos++];
        uint8_t trigger_type = payload[pos++];
        uint8_t trigger_angel = payload[pos++];
        uint32_t snapshot_timestamps = (uint32_t) payload[pos + 0] |  // 最低字节在最低地址
                                       (uint32_t) payload[pos + 1] << 8 |
                                       (uint32_t) payload[pos + 2] << 16 |
                                       (uint32_t) payload[pos + 3] << 24;
        pos += 4;
        uint32_t size = (uint32_t) payload[pos + 0] |  // 最低字节在最低地址
                        (uint32_t) payload[pos + 1] << 8 |
                        (uint32_t) payload[pos + 2] << 16 |
                        (uint32_t) payload[pos + 3] << 24;
        pos += 4;
        info.id = (uint16_t) id;
        info.action_id_index = 0;
        info.action_id_timestamps = 0;
        info.trigger_type = trigger_type;
        info.trigger_angel = trigger_angel;
        info.snapshot_timestamps = snapshot_timestamps;
        info.size = size;
        memcpy(info.md5, payload + pos, 16);
        pos += 16;
        infos[i] = info;
    }
    *info_size = pic_number;

    return 0;
}

/**
 * 对应 删除图片 hd_host_auto_pic_delete_encode
 */

uint8_t hd_slave_auto_pic_delete_decode(
        uint8_t *pic_id,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (pic_id == NULL) {
        return 2;
    }
    if (payload == NULL || payload_size != 1) {
        return 3;
    }
    *pic_id = payload[0];
    return 0;
}

/**
 * 对应 删除图片 hd_slave_auto_pic_delete_encode
 */
uint8_t hd_host_auto_pic_delete_decode(
        uint8_t *result,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (result == NULL) {
        return 2;
    }
    if (payload == NULL || payload_size != 1) {
        return 3;
    }
    *result = payload[0];
    return 0;
}

/**
 * 对应 拉取图片 hd_host_auto_pic_pull_encode
 */

uint8_t hd_slave_auto_pic_pull_decode(
        uint8_t *pic_id,
        uint32_t *offset,
        uint32_t *len,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (pic_id == NULL || offset == NULL || len == NULL) {
        return 2;
    }
    if (payload == NULL || payload_size != 9) {
        return 3;
    }
    *pic_id = payload[0];
    *offset = (uint32_t) payload[1] |
              (uint32_t) payload[2] << 8 |
              (uint32_t) payload[3] << 16 |
              (uint32_t) payload[4] << 24;
    *len = (uint32_t) payload[5] |
           (uint32_t) payload[6] << 8 |
           (uint32_t) payload[7] << 16 |
           (uint32_t) payload[8] << 24;
    return 0;
}

/**
 * 对应 拉取图片 hd_slave_auto_pic_pull_encode
 */
uint8_t hd_host_auto_pic_pull_decode(
        uint8_t *result,
        uint8_t *data,
        uint32_t *data_size,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (result == NULL || data == NULL) {
        return 1;
    }
    if (payload == NULL) {
        return -2;
    }

    *result = payload[0];
    uint32_t pos = 1;
    for (uint32_t i = 0; i < payload_size; ++i) {
        data[i] = payload[pos];
        pos++;
    }
    *data_size = payload_size - 1;
    return 0;
}


/**
 * 对应 拉取图片完成 hd_host_auto_pic_pull_completed_encode
 */

uint8_t hd_slave_auto_pic_pull_completed_decode(
        const unsigned char *payload,
        uint32_t payload_size
) {
    return 0;
}

/**
 * 对应 拉取图片完成 hd_slave_auto_pic_pull_completed_encode
 */
uint8_t hd_host_auto_pic_pull_completed_decode(
        uint8_t *result,
        const unsigned char *payload,
        uint32_t payload_size
) {
    if (result == NULL) {
        return 2;
    }
    if (payload == NULL || payload_size != 1) {
        return 3;
    }
    *result = payload[0];
    return 0;
}
//</editor-fold>


//<editor-fold desc="私有方法">
/* ******************************* 私有方法 ****************************** */
/* ******************************* 私有方法 ****************************** */
/* ******************************* 私有方法 ****************************** */
/**
    1字节	1字节	1字节	4字节	4字节	16字节
    pic_id	触发方式	触发角度	抓取时间	图片大小	MD5
 */
static uint8_t hd_dynamic_pic_infos_encode_simple(
        hd_dynamic_pic_info *infos,
        uint32_t count,
        unsigned char **result,
        uint32_t *result_size
) {
    if (infos == NULL) return 33;
    const size_t total_size = 1 + 27 * count;
    unsigned char *buffer = (unsigned char *) malloc(total_size);
    if (buffer == NULL) {
        return 22;
    }
    // 逐个复制结构体到缓冲区
    unsigned char *ptr = buffer;
    *ptr++ = count;
    for (size_t i = 0; i < count; i++) {
        const hd_dynamic_pic_info info = infos[i];

        // 复制id (1字节)
        *ptr++ = ((info.id >> 0) & 0xFF);
        *ptr++ = info.trigger_type;
        *ptr++ = info.trigger_angel;
        *ptr++ = (info.snapshot_timestamps >> 0) & 0xFF;  // LSB
        *ptr++ = (info.snapshot_timestamps >> 8) & 0xFF;
        *ptr++ = (info.snapshot_timestamps >> 16) & 0xFF;
        *ptr++ = (info.snapshot_timestamps >> 24) & 0xFF; // MSB
        *ptr++ = (info.size >> 0) & 0xFF;  // LSB
        *ptr++ = (info.size >> 8) & 0xFF;
        *ptr++ = (info.size >> 16) & 0xFF;
        *ptr++ = (info.size >> 24) & 0xFF; // MSB
        // 复制md5 (16字节) 数组 不受大小端影响
        memcpy(ptr, info.md5, 16);
        ptr += 16;
    }
    *result_size = total_size;
    *result = buffer;
    return 0;
}
//</editor-fold>


