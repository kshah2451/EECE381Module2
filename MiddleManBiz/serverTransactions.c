#include <stdio.h>
#include "altera_up_avalon_rs232.h"
#include <string.h>
#include <system.h>
#include <stdlib.h>
#include <altera_up_sd_card_avalon_interface.h>

#define MAX_FILENAME 12


int main() {
	int i;

	//1 for receiving file from middleman, 2 for sending to middleman
	int mode;

	//Num char in filename array
	int numFileName;
	//Num bytes in file
	int numBytesFile;

	//Num characters in file array
	int numFile;

	char* listName;



	//File to send or receive (likely a piece of it)
	unsigned char fileName[MAX_FILENAME];

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

		if(!alt_up_sd_card_is_FAT16()){
			printf("SD CARD is not FAT16 Format\n");
		}

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
				//mode -= 48;

				printf("Mode:%d\n", mode);

				//Receive file from middleman and save to SD
				if (mode == 1) {

					printf("Waiting for num char:\n");
					// The second byte is the number of characters in the file name
					while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
					alt_up_rs232_read_data(uart, &data, &parity);
					numFileName = (int) data;
					//numFileName -= 48;

					//Now receive the file name
					printf("About to receive %d characters:\n\n", numFileName);
					printf("Filename received:");
					for (i = 0; i < numFileName; i++) {
						while (alt_up_rs232_get_used_space_in_read_FIFO(uart)== 0);
						alt_up_rs232_read_data(uart, &data, &parity);
						fileName[i] = data;
						printf("%c", data);
					}
					printf("\n");
					fileName[i] = '.';
					fileName[i+1] = 't';
					fileName[i+2] = 'x';
					fileName[i+3] = 't';
					fileName[i+4]= '\0';
					//
					// TODO:
					// USE THAT FILENAME TO MAKE A NEW FILE ON SD CARD HERE

					handle = alt_up_sd_card_fopen(fileName, 1);
					if(handle < 0){
						//TODO: File can't be opened, do something about it
						printf("send had a neg handle \n\n");
					}
					else{
						// The 4 bytes after filename is the number of bytes in the file
						//
						// SHIFT BYTES LEFT AND CONCATENATE TO ACCOUNT FOR SEVERAL BYTES WORTH OF FILE
						// WRITE FIRST 4 BYTES OF FILE AS SIZE OF FILE IN BYTES
						while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
						alt_up_rs232_read_data(uart, &data, &parity);
						alt_up_sd_card_write(handle, data);
						numBytesFile = (int) data;
						numBytesFile = numBytesFile << 8;

						while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
						alt_up_rs232_read_data(uart, &data, &parity);
						alt_up_sd_card_write(handle, data);
						numBytesFile += (int) data;
						numBytesFile = numBytesFile << 8;

						while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
						alt_up_rs232_read_data(uart, &data, &parity);
						alt_up_sd_card_write(handle, data);
						numBytesFile += (int) data;
						numBytesFile = numBytesFile << 8;

						while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
						alt_up_rs232_read_data(uart, &data, &parity);
						alt_up_sd_card_write(handle, data);
						numBytesFile += (int) data;

						//WRITE BYTES TO SD CARD
						printf("About to receive %d file chars:\n\n",numBytesFile);
						for (i = 0; i < numBytesFile; i++) {

							while (alt_up_rs232_get_used_space_in_read_FIFO(uart)== 0);
							alt_up_rs232_read_data(uart, &data, &parity);
							alt_up_sd_card_write(handle, data);
						}
						printf("File done\n");

						alt_up_sd_card_fclose(handle);

					}


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
					//numFileName -= 48;

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

					fileName[i] = '.';
					fileName[i+1] = 't';
					fileName[i+2] = 'x';
					fileName[i+3] = 't';
					fileName[i+4]= '\0';


					handle = alt_up_sd_card_fopen(fileName, 0);

					if (handle == -1) {
						//SEND ANDROID AN ASCII 2 TO LET THEM KNOW TO RECEIVE FILE NAME
						alt_up_rs232_write_data(uart, 50);
						printf("neg handle");
						if(alt_up_sd_card_find_first(".",listName) ==-1){
							//SEND ANDROID AN ASCII 2 TO LET THEM KNOW THERES NO FILE NAMES
							alt_up_rs232_write_data(uart, 50);
							printf("no files");
						}
						else{
							//SEND ANDROID LIST OF FILE NAMES
							printf("some files");
							i=0;
							for(i = 0; listName[i] != '.'; i++){
								alt_up_rs232_write_data(uart, listName[i]);
							}
							alt_up_rs232_write_data(uart, 32);
							while(alt_up_sd_card_find_next(listName)!=-1){
								for(i = 0; listName[i] != '.'; i++){
									alt_up_rs232_write_data(uart, listName[i]);
								}
								alt_up_rs232_write_data(uart, 32);
							}
							alt_up_rs232_write_data(uart, 1);
							printf("done files");
						}
					} else {

						//SEND ANDROID AN ASCII 1 TO LET THEM KNOW TO RECEIVE A FILE
						alt_up_rs232_write_data(uart, 49);



						// SHIFT BYTES LEFT AND CONCATENATE TO ACCOUNT FOR SEVERAL BYTES WORTH OF FILE
						// WRITE FIRST 4 BYTES OF FILE AS SIZE OF FILE IN BYTES

						data = alt_up_sd_card_read(handle);
						alt_up_rs232_write_data(uart, data);
						numFile = (int) data;
						numFile = numFile << 8;

						data = (int) alt_up_sd_card_read(handle);
						alt_up_rs232_write_data(uart, data);
						numFile += (int) data;
						numFile = numFile << 8;

						data = (int) alt_up_sd_card_read(handle);
						alt_up_rs232_write_data(uart, data);
						numFile += (int) data;
						numFile = numFile << 8;

						data = (int) alt_up_sd_card_read(handle);
						alt_up_rs232_write_data(uart, data);
						numFile += (int) data;

						printf("About to send %d file bytes\n", numFile);
						while (numFile > 0) {

							if(numFile % 100 == 0){
								for(i = 0; i<12000;i++);
							}

							data = alt_up_sd_card_read(handle);
							alt_up_rs232_write_data(uart, data);

							numFile--;



						}

						// WRITE A "FILE DONE" STRING OR WHATEVER WE DECIDE
						printf("sending end bits\n");
					//	alt_up_rs232_write_data(uart, 1);


						//
						//
						//
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
