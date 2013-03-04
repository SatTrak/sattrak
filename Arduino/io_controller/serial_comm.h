// Packet constants
#define PACKET_LEN 24 // bytes
#define HEADER 0xAA
#define LOC_HEADER 0
#define LOC_COMMAND 1
#define LOC_ARGS 2

// Commands and arguments
#define COMMAND_ACK 0x01
#define LOC_ACKD_COMMAND 0
#define LEN_ACKD_COMMAND 1

#define COMMAND_NACK 0x02
#define LOC_NACKD_COMMAND 0
#define LEN_NACKD_COMMAND 1

#define COMMAND_ORNT_SET 0x03
#define LOC_AZIMUTH 0
#define LEN_AZIMUTH 8
#define LOC_ELEVATION 8
#define LEN_ELEVATION 8

#define COMMAND_ORNT_READ 0x04
// No args

#define COMMAND_ORNT_RESPONSE 0x05
// Same args as COMMAND_ORNT_SET

#define COMMAND_ENV_READ 0x06
// No args

#define COMMAND_ENV_RESPONSE = 0x07;
#define LOC_TEMP 0
#define LEN_TEMP 8
#define LOC_HUMID 8
#define LEN_HUMID 8

#define COMMAND_GPS_READ 0x08
// No args

#define COMMAND_GPS_RESPONSE 0x09
#define LOC_LAT 0
#define LEN_LAT 8
#define LOC_LONG 8
#define LEN_LONG 8

#define COMMAND_EST_CONN  0x0A
// No args
