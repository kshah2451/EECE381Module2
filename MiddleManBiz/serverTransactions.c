#include <stdio.h>
#include "altera_up_avalon_rs232.h"
#include <string.h>


#define MAX_FILENAME 30
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







	printf("UART Initialization\n");
	alt_up_rs232_dev* uart = alt_up_rs232_open_dev("/dev/rs232_0");

	while (1) {

		printf("Clearing read buffer to start\n");
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart)) {
			alt_up_rs232_read_data(uart, &data, &parity);
		}

		// Now receive the instruction from the Middleman
		printf("Waiting for instruction to come from the Middleman\n");
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);

		// First byte is the mode, 1 for receiving file from middleman, 2 for sending to middleman
		alt_up_rs232_read_data(uart, &data, &parity);
		mode =(int) data;
		mode -= 48;

		printf("Mode:%d\n", mode);

		//Receive file from middleman and save to SD
		if(mode == 1){


			printf("Waiting for num char:\n");
			// The second byte is the number of characters in the file name
			while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
			alt_up_rs232_read_data(uart, &data, &parity);
			numFileName = (int)data;
			numFileName -= 48;

			//Now receive the file name
			printf("About to receive %d characters:\n\n", numFileName);
			printf("Filename received:");
			for (i = 0; i < numFileName; i++) {
				while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
				alt_up_rs232_read_data(uart, &data, &parity);

				fileName[i] = data;

				printf("%c", data);
			}
			printf("\n");


			//
			//
			//
			// TODO:
			// USE THAT FILENAME TO MAKE A NEW FILE ON SD CARD HERE
			//
			//
			//
			//
			//




			// The byte(s)? after filename is the number of packets in the file
			//
			//
			//
			// TODO: SHIFT BYTES LEFT AND CONCATENATE TO ACCOUNT FOR SEVERAL BYTES WORTH OF PACKET AMOUNT
			//
			//
			//
			printf("Waiting for num packets\n");
			while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
			alt_up_rs232_read_data(uart, &data, &parity);
			numPackets = (int)data;
			numPackets -= 48;

			//Now receive the file
			//We receive packets of a fixed size, all packets are full
			//After receiving all full packets, receive a byte of characters still left
			// Then read the remaining characters
			printf("About to receive %d packets:\n\n", numPackets);
			for (i = 0; i < numPackets; i++) {
				for (j = 0; j < MAX_PACKETSIZE; j++) {

					while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
					alt_up_rs232_read_data(uart, &data, &parity);

					//file[i] = data;
					//
					//
					// TODO:
					// Dump this into SD card, maybe just straight up use data char instead of putting it into a file array
					//
					//
					//

				}
			}
			printf("Packets received, waiting for leftover\n");


			// This byte is the number of characters left in file not filling a packet
			while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
			alt_up_rs232_read_data(uart, &data, &parity);
			numLeftover = (int)data;
			numLeftover -= 48;


			printf("About to receive %d leftover chars:\n\n", numLeftover);
			for (i = 0; i < numLeftover; i++) {

				while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0);
				alt_up_rs232_read_data(uart, &data, &parity);

				//file[i] = data;
				//
				//
				// TODO:
				// Dump this into SD card, maybe just straight up use data char instead of putting it into a file array
				//
				//
				//


			}
			printf("Leftover received, file done\n");


			//
			//
			//
			//TODO: close up the SD card here
			//
			//
			//



			//This bracket ends receiving a file
		}


		//Send file to middleman from SD
		else if(mode == 2){





		//This bracket ends sending a file
		}

		//Something broke
		else{
			printf("Wrong mode, something broke, starting over\n");
		}






/*


		printf("Sending the message to the Middleman\n");

		// Start with the number of bytes in our message

		alt_up_rs232_write_data(uart, (unsigned char) strlen(message));

		// Now send the actual message to the Middleman

		for (i = 0; i < strlen(message); i++) {
			alt_up_rs232_write_data(uart, message[i]);
		}

		// Now receive the message from the Middleman

		printf("Waiting for data to come back from the Middleman\n");
		while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
			;

		// First byte is the number of characters in our message

		alt_up_rs232_read_data(uart, &data, &parity);
		int num_to_receive = (int) data;

		printf("About to receive %d characters:\n", num_to_receive);

		for (i = 0; i < num_to_receive; i++) {
			while (alt_up_rs232_get_used_space_in_read_FIFO(uart) == 0)
				;
			alt_up_rs232_read_data(uart, &data, &parity);

			printf("%c", data);
		}
		printf("\n");
		printf("Message Echo Complete\n");

		*/

	//end while 1
	}
	return 0;
//end main
}
