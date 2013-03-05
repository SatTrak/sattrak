// ======================= 
// COMMUNICATION FUNCTIONS
// =======================

void serial_init() {
  // Start serial communication at BAUD_RATE
  Serial.begin(BAUD_RATE);
}

// Send a packet with the given command and arguments over serial
void send_packet(byte command, byte *args, int args_length) {
  // Create byte array to send
  byte packet_bytes[PACKET_LEN];

  // Set all elements of array to zero
  memset(packet_bytes, 0, sizeof(packet_bytes));

  // Insert header and command
  packet_bytes[LOC_HEADER] = HEADER;
  packet_bytes[LOC_COMMAND] = command;

  // Insert arguments
  for (int i = 0; i < args_length; i++) {
    packet_bytes[LOC_ARGS + i] = args[i];
  }

  // Write the packet to the serial port and then flush the output
  Serial.write(packet_bytes, PACKET_LEN);
  Serial.flush();
}

// Send the given byte array over serial
void send_packet(byte *packet_bytes) {
  Serial.write(packet_bytes, PACKET_LEN);
  Serial.flush();
}

// Receive a packet of PACKET_LEN bytes over serial and respond to it
void recv_and_handle_packet() {
  // Initialize a byte array to read in a packet
  char packet_bytes[PACKET_LEN];

  int num_bytes = Serial.readBytes(packet_bytes, PACKET_LEN);

  // If data was received, handle the packet
  if (num_bytes > 0) {
    handle_packet((byte *) packet_bytes);
  }
}

// Extract a byte array of arguments from the given packet
byte *get_args(byte *packet_bytes, int args_length) {
  byte args[args_length];
  
  // Extract arguments
  for (int i = 0; i < args_length; i++) {
    args[i] = packet_bytes[LOC_ARGS + i];
  }
  return args;
}

// Handle a received packet
void handle_packet(byte *packet_bytes) {

  // Do nothing if the packet doesn't have the right header
  if (!has_header(packet_bytes)) {
    return;
  }

  byte command = packet_bytes[LOC_COMMAND];

  switch (command) {
    case (COMMAND_ACK): 
    {
      // Handle ack
      break;
    }
    case (COMMAND_NACK): 
    {
      // Handle nack
      break;
    }
    case (COMMAND_ORNT_SET): 
    {
      // Handle orientation set
      // Respond with an ack
      byte resp_command = COMMAND_ACK;
      int resp_args_length = LEN_ACKD_COMMAND;
      byte resp_args[resp_args_length];
      resp_args[LOC_ACKD_COMMAND] = command;
      send_packet(resp_command, resp_args, resp_args_length);
      
      // Extract arguments and get angles
      int args_length = LEN_AZIMUTH + LEN_ELEVATION;
      byte *args = get_args(packet_bytes, args_length);
      double az_target = extract_double(args, LOC_AZIMUTH);
      double el_target = extract_double(args, LOC_ELEVATION);
      
      // Move motors
      set_azimuth(az_target);
      set_elevation(el_target);
      
      // Read new orientation
      read_compass();
      read_inclinometer();
      
      // Send orientation response to update RPi of new location
      int ornt_args_length = LEN_AZIMUTH + LEN_ELEVATION;
      byte ornt_args[ornt_args_length];
      insert_double(ornt_args, LOC_AZIMUTH, azimuth);
      insert_double(ornt_args, LOC_ELEVATION, elevation);
      send_packet(COMMAND_ORNT_RESPONSE, ornt_args, ornt_args_length);
      break;
    }
    case (COMMAND_ORNT_READ): 
    {
      // Handle orientation read
      // Get data
      read_compass();
      read_inclinometer();
      
      // Send response packet
      byte resp_command = COMMAND_ORNT_RESPONSE;
      int args_length = LEN_AZIMUTH + LEN_ELEVATION;
      byte resp_args[args_length];
      insert_double(resp_args, LOC_AZIMUTH, azimuth);
      insert_double(resp_args, LOC_ELEVATION, elevation);
      send_packet(resp_command, resp_args, args_length);
      break;
    }
    case (COMMAND_ENV_READ): 
    {
      // Handle environmental read
      // Get data
      read_env();

      // Send response packet
      byte resp_command = COMMAND_ENV_RESPONSE;
      int args_length = LEN_TEMP + LEN_HUMID;
      byte resp_args[args_length];
      insert_double(resp_args, LOC_TEMP, temperature);
      insert_double(resp_args, LOC_HUMID, humidity);
      send_packet(resp_command, resp_args, args_length);
      break;
    }
    case (COMMAND_GPS_READ): 
    {
      // Handle GPS read
      // Get latitude
      read_gps();

      // Send response packet
      byte resp_command = COMMAND_GPS_RESPONSE;
      int args_length = LEN_LAT + LEN_LONG;
      byte resp_args[args_length];
      insert_double(resp_args, LOC_LAT, latitude);
      insert_double(resp_args, LOC_LONG, longitude);
      send_packet(resp_command, resp_args, args_length);
      break;
    }
    case (COMMAND_EST_CONN):
    {
      // Respond with an Ack to establish the connection
      byte resp_command = COMMAND_ACK;
      int args_length = LEN_ACKD_COMMAND;
      byte resp_args[args_length];
      resp_args[LOC_ACKD_COMMAND] = command;
      send_packet(resp_command, resp_args, args_length);
    }
  default: 
    {
      // Invalid command received
      // Send nack
      byte resp_command = COMMAND_NACK;
      int args_length = LEN_NACKD_COMMAND;
      byte resp_args[args_length];
      resp_args[LOC_NACKD_COMMAND] = command;
      send_packet(resp_command, resp_args, args_length);
      break;
    }
  }

}  

boolean has_header(byte *packet_bytes) {
  return packet_bytes[LOC_HEADER] = HEADER;
}


