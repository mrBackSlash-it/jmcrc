package it.mrbackslash.jmcrc;
import com.sun.istack.internal.NotNull;
import java.lang.System;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.charset.Charset;

/**
 * PacketAssembler class. Assembles valid packets for RCON protocol + utilities.
 * @see <a href="https://github.com/mrBackSlash-it/jmcrc">GitHub Repository</a>
 * @author mrBackSlash-it
 */
public class PacketAssembler {
    private static final byte[] padding = {0x00, 0x00};

    /**
     * Assembles a packet valid for the RCON Protocol
     * @param requestId  Request identifier
     * @param requestType Request type: 3 for login, 2 to run a command
     * @param payload Content of the request, can be the password or the command
     * @throws InvalidPayloadJmcrcException Packet is not valid
     * @return byte[]
     */
    public static byte[] AssemblePacket(int requestId, int requestType, @NotNull String payload) throws InvalidPayloadJmcrcException{
        //checks if payload is pure ascii
        if(!isAscii(payload)){
            throw new InvalidPayloadJmcrcException();
        }
        //calculates the total length of the packet, length of the remainder and creates data array
        byte[] bPayload = payload.getBytes();
        int totLength = 14 + bPayload.length;
        if(totLength >= 1446){
            throw new InvalidPayloadJmcrcException();
        }
        int remainderLength = 10 + bPayload.length;
        byte[] data = new byte[totLength];
        //converts parameters to byte[]
        byte[] bRequestId = intToByteArray(requestId);
        byte[] bRequestType = intToByteArray(requestType);
        byte[] bRemainderLength = intToByteArray(remainderLength);
        //assembling packet
        System.arraycopy(bRemainderLength, 0, data, 0, 4);
        System.arraycopy(bRequestId, 0, data, 4, 4);
        System.arraycopy(bRequestType, 0, data, 8, 4);
        System.arraycopy(bPayload, 0, data, 12, bPayload.length);
        System.arraycopy(padding, 0, data, 12+ bPayload.length, 2);
        return data;
    }

    /**
     * Converts an int to a byte[]
     * @param i Integer to convert
     * @return byte[]
     */
    public static byte[] intToByteArray (int i) {
        ByteBuffer buf = ByteBuffer.allocate(4);
        buf.order(ByteOrder.LITTLE_ENDIAN);
        buf.putInt(i);
        return buf.array();
    }

    /**
     * Checks if the string is pure ASCII
     * @param s String to check
     * @return boolean
     */
    private static boolean isAscii (@NotNull String s){
        return Charset.forName("US-ASCII").newEncoder().canEncode(s);
    }
}
