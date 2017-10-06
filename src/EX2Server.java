import java.io.InputStream;
import java.io.OutputStream;
import java.net.BindException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Random;
import java.util.zip.CRC32;

public class EX2Server {
	public static void main(String[] args) throws Exception {
		try (ServerSocket serverSocket = new ServerSocket(38102)) {
			System.out.println("Waiting for connection...");
			while (true) {
				Socket socket = serverSocket.accept();
				String address = socket.getInetAddress().getHostAddress();
				System.out.printf("Client connected: %s%n", address);
				Random rand = new Random();

				OutputStream os = socket.getOutputStream();
				byte[] bytes = new byte[100];
				byte[] bytesHalved = new byte[200];
				rand.nextBytes(bytes);
				System.out.print("Sent bytes:");
				for ( int i = 0; i<bytes.length; i++ ){
					if ( i % 10 == 0){
						System.out.print("\n   ");
					}
					System.out.print(String.format("%02X", bytes[i]));
					bytesHalved[i*2] = (byte)( Integer.divideUnsigned((int)bytes[i], 0x10) & 0xF);
					bytesHalved[i*2+1] = (byte) (bytes[i] & 0x0F);
				}
				System.out.println();

				os.write(bytesHalved);

				CRC32 crc = new CRC32();
				crc.update(bytes);
				long crcVal = crc.getValue();
				System.out.println("Generated CRC Value: " + Long.toHexString(crcVal).toUpperCase() );

				InputStream is = socket.getInputStream();
				byte[] recvCRC = new byte[4];
				is.read(recvCRC);
				int result = 1;
				for ( int i = 3; i >= 0; i--){
					if ( recvCRC[i] != (byte)(crcVal & 0xFF)){
						result = 0;
					}
					crcVal = crcVal / 0x100;
				}
				
				os.write(result);
				if ( result == 1 ) {
					System.out.println("Client CRC Good");
				} else  {
					System.out.println("Client CRC Incorrect");
				}
				socket.close();
			}
		} catch ( BindException e ){
			System.out.println( "A server is already running, terminating...");
		}
	}
}
