#include <stdio.h>
#include "altera_up_avalon_rs232.h"
#include <string.h>
#include <system.h>
#include <stdlib.h>
#include <altera_up_sd_card_avalon_interface.h>

#define MAX_FILENAME 7
#define MAX_PACKETSIZE 256

int main() {
	int i;
	int j;

	//1 for receiving file from middleman, 2 for sending to middleman
	int mode;

	//Num char in filename array
	int numFileName;
	//Num packets in the file
	int numPackets;
	//Num leftover characters in the file not in a packet
	int numLeftover;
	//Array to hole filename
	unsigned char fileName[MAX_FILENAME];
	//Num characters in file array
	int numFile;
	//File to send or receive (likely a piece of it)
	unsigned char file[MAX_PACKETSIZE];

	//variable to hold data received from uart
	unsigned char data;
	//parity bit for reading (not using parity atm, but still need the bit)
	unsigned char parity;

	short int handle;
	//handle for the file to create/access
	int connected = 0;
	//Variable to keep track of whether the SD CARD is connected or not.

	while (1) {
		alt_up_sd_card_dev *device_reference = NULL;
		device_reference = alt_up_sd_card_open_dev(ALTERA_UP_SD_CARD_AVALON_INTERFACE_0_NAME);

		printf("UART Initialization\n");
		alt_up_rs232_dev* uart = alt_up_rs232_open_dev("/dev/rs232_0");

		if (device_reference != NULL) {

			while (alt_up_sd_card_is_Present()) {

				printf("Clearing read buffer to start\n");
				while (alt_up_rs232_get_used_space_in_read_FIFO(uart)) {
					alt_up_rs232_read_data(uart, &data, &parity);
				}

				// Now receive the instruction from the Middleman
				printf("Waiting for instruction to come from the Middleman\n");
				while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
					;

				// First byte is the mode, 1 for receiving file from middleman, 2 for sending to middleman
				alt_up_rs232_read_data(uart, &data, &parity);
				mode = (int) data;
				mode -= 48;

				printf("Mode:%d\n", mode);

				//Receive file from middleman and save to SD
				if (mode == 1) {

					printf("Waiting for num char:\n");
					// The second byte is the number of characters in the file name
					while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
						;
					alt_up_rs232_read_data(uart, &data, &parity);
					numFileName = (int) data;
					numFileName -= 48;

					//Now receive the file name
					printf("About to receive %d characters:\n\n", numFileName);
					printf("Filename received:");
					for (i = 0; i < numFileName; i++) {
						while (alt_up_rs232_get_used_space_in_read_FIFO(uart)
								== 0)
							;
						alt_up_rs232_read_data(uart, &data, &parity);

						fileName[i] = data;

						printf("%c", data);
					}
					printf("\n");

					//
					// TODO:
					// USE THAT FILENAME TO MAKE A NEW FILE ON SD CARD HERE

					handle = alt_up_sd_card_fopen(&fileName, 1);
					// The byte(s)? after filename is the number of packets in the file
					//


					//TODO:  send andropid notice of whether file can open or not


					// TODO: SHIFT BYTES LEFT AND CONCATENATE TO ACCOUNT FOR SEVERAL BYTES WORTH OF PACKET AMOUNT
					//

					printf("Waiting for num packets\n");
					while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
						;
					alt_up_rs232_read_data(uart, &data, &parity);
					numPackets = (int) data;
					numPackets -= 48;

					//Now receive the file
					//We receive packets of a fixed size, all packets are full
					//After receiving all full packets, receive a byte of characters still left
					// Then read the remaining characters
					printf("About to receive %d packets:\n\n", numPackets);
					for (i = 0; i < numPackets; i++) {
						for (j = 0; j < MAX_PACKETSIZE; j++) {

							while (alt_up_rs232_get_used_space_in_read_FIFO(
									uart) == 0)
								;
							alt_up_rs232_read_data(uart, &data, &parity);

							//file[i] = data;
							//
							//
							// TODO:
							// Dump this into SD card, maybe just straight up use data char instead of putting it into a file array
							//TODO: ERROR CHECKing for successful/unsucessful writes, alt_up_sd_card_write returns a bool
							alt_up_sd_card_write(handle, data);
						}
					}
					printf("Packets received, waiting for leftover\n");

					// This byte is the number of characters left in file not filling a packet
					while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
						;
					alt_up_rs232_read_data(uart, &data, &parity);
					numLeftover = (int) data;
					numLeftover -= 48;

					printf("About to receive %d leftover chars:\n\n",
							numLeftover);
					for (i = 0; i < numLeftover; i++) {

						while (alt_up_rs232_get_used_space_in_read_FIFO(uart)
								== 0)
							;
						alt_up_rs232_read_data(uart, &data, &parity);

						//file[i] = data;
						//
						//
						// TODO:
						// Dump this into SD card, maybe just straight up use data char instead of putting it into a file array
						alt_up_sd_card_write(handle, data);
					}
					printf("Leftover received, file done\n");

					//TODO: close up the file here
					//TODO: error checking
					alt_up_sd_card_fclose(handle);
					//This bracket ends receiving a file
				}

				//Send file to middleman from SD
				else if (mode == 2) {

					printf("Waiting for num char:\n");
					// The second byte is the number of characters in the file name
					while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
						;
					alt_up_rs232_read_data(uart, &data, &parity);
					numFileName = (int) data;
					numFileName -= 48;

					//Now receive the file name
					printf("About to receive %d characters:\n\n", numFileName);
					printf("Filename received:");
					for (i = 0; i < numFileName; i++) {
						while (alt_up_rs232_get_used_space_in_read_FIFO(uart)
								== 0)
							;
						alt_up_rs232_read_data(uart, &data, &parity);

						fileName[i] = data;

						printf("%c", data);
					}
					printf("\n");

					//
					//
					//TODO:  IF FILE FOUND SEND A 1, IF NOT FOUND SEND A 2 AND THEN SEND LIST OF FILE NAME
					handle = alt_up_sd_card_fopen(&fileName, 1);

					if (handle == -1) {
						alt_up_rs232_write_data(uart, 50);

						//TODO SEND LIST OF ALL FILE NAMES
					} else {

						// WE NEED TO CHANGE THE NEXT WHILE LOOP INTO A WHILE THERES STILL FILE TO SEND


						printf("About to send file\n");

						//TODO:  MAKE THIS WHILE LOOP A WHILE STILL FILE LEFT TO SEND
						data = alt_up_sd_card_read(handle);
						while (data != -2) {

							//TODO: PUT THE CHARACTER TO SEND AS THE SECOND PARAMETER, MUST BE UNSIGNED CHAR
							alt_up_rs232_write_data(uart, data);
							data = alt_up_sd_card_read(handle);

						}

						//TODO: WRITE A "FILE DONE" STRING OR WHATEVER WE DECIDE

						alt_up_rs232_write_data(uart, 1);
						alt_up_rs232_write_data(uart, 2);
						alt_up_rs232_write_data(uart, 3);
						alt_up_rs232_write_data(uart, 4);

						//
						//
						//
						//TODO: close up the file here
						alt_up_sd_card_fclose(handle);

					}

					//This bracket ends sending a file
				}

				//Something broke
				else {
					printf("Wrong mode, something broke, starting over\n");
				}
			}
		}

		else {
			printf("Card Reader is not working\n");
		}
	}
	return 0;
	//end main
}
