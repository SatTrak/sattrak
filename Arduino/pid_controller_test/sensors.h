// ======================= 
// SENSOR CONSTANTS
// =======================

// Compass
#define HMC6352SlaveAddress 0x21
#define HMC6352ReadAddress 0x41 //"A" in hex

// Inclinometer
#define INC_READ_RDAX 0x10 // read RDAX
#define INC_OFFSET 1024.0  // digital offset value
#define INC_SENS 819.0     // inclinometer sensitivity
