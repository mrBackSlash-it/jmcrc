/*
This software is distributed under the Apache License 2.0
Copyright 2020 Vittorio Lo Mele

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/

package it.mrbackslash.jmcrc;
import org.jetbrains.annotations.NotNull;
import java.util.Random;
import java.io.*;
import java.net.Socket;
import java.util.Arrays;

/**
* RCON Client for Minecraft servers implemented in Java [VERSION 1.1]
* @see <a href="https://github.com/mrBackSlash-it/jmcrc">GitHub Repository</a>
* @author mrBackSlash-it
 */

public class Jmcrc {
    private String chost;
    private int cport;
    private boolean loggedIn;
    private int requestId;
    private Socket socket;
    private OutputStream out;
    private InputStream in;
    private byte[] dataBuffer;

    /**
     * Creates a connection to the server and sends a login packet.
     * @param host Hostname or IP Address of the Minecraft Server
     * @param port TCP Port where server RCON is listening, usually 25575
     * @param password RCON Password
     * @return boolean (True if login was successful)
     * @throws UnableToConnectJmcrcException Throwed when server is unreachable
     * @throws IOException Socket error
     * @throws InvalidPayloadJmcrcException The payload is not valid (either too long or not ASCII)
     */
    public boolean init(@NotNull String host, int port, @NotNull String password) throws UnableToConnectJmcrcException, IOException, InvalidPayloadJmcrcException {
        if(isConnected()){
            throw new UnableToConnectJmcrcException();
        }
        try{
            socket = new Socket(host, port);
            out = socket.getOutputStream();
            in = socket.getInputStream();
        }catch(Exception x) {
            throw new UnableToConnectJmcrcException();
        }
        //generate request id
        Random rand = new Random();
        requestId = rand.nextInt(2147483647);
        //assemble login packet
        byte[] loginPacket = PacketAssembler.AssemblePacket(requestId, PacketAssembler.TYPE_LOGIN, password);
        //write login packet
        out.write(loginPacket);
        //creating data buffer
        dataBuffer = new byte[8192];
        //noinspection ResultOfMethodCallIgnored
        in.read(dataBuffer);
        //copying response id to validate login
        byte[] responseId = new byte[4];
        System.arraycopy(dataBuffer,4, responseId, 0, 4);
        //reading response id, if is the same as request id for login, the operation was successful
        if(!Arrays.equals(PacketAssembler.intToByteArray(requestId), responseId)){
            return false;
        }
        //set class variables
        chost = host;
        cport = port;
        loggedIn = true;
        //clean buffer and return
        dataBuffer = new byte[8192];
        return true;
    }

    /**
     * Sends a command to a server after the connection has been initialized
     * @param payload Command to send
     * @return String (server response)
     * @throws IOException Socket fail
     * @throws NotConnectedJmcrcException You aren't connected to any server.
     * @throws InvalidPayloadJmcrcException Invalid payload (either too long or non-ASCII)
     * @throws InvalidPacketJmcrcException Invalid packet received from the server or passed to the function DisassemblePacket
     */
    public String send(String payload) throws IOException, NotConnectedJmcrcException, InvalidPayloadJmcrcException, InvalidPacketJmcrcException{
        if(!isConnected()){
            throw new NotConnectedJmcrcException();
        }
        //assemble login packet
        byte[] requestPacket = PacketAssembler.AssemblePacket(requestId, PacketAssembler.TYPE_COMMAND, payload);
        //write login packet
        out.write(requestPacket);
        //creating data buffer
        dataBuffer = new byte[8192];
        //noinspection ResultOfMethodCallIgnored
        in.read(dataBuffer);
        byte[] bResponse = PacketDisassembler.disassemblePacket(dataBuffer, PacketDisassembler.PACKET_S_PAYLOAD);
        //clean buffer and return
        dataBuffer = new byte[8192];
        return PacketDisassembler.rawPayloadToASCII(bResponse);
    }

    /**
     * Closes the session and disconnects from the server.
     * @throws IOException Socket fail
     * @throws NotConnectedJmcrcException You aren't connected to any server.
     */
    public void disconnect() throws IOException, NotConnectedJmcrcException{
        if(!isConnected()){
            throw new NotConnectedJmcrcException();
        }
        in.close();
        out.close();
        socket.close();
        chost = "";
        cport = 0;
        loggedIn = false;
        dataBuffer = new byte[8192];
    }

    /**
     * Gets the state of connection
     * @return boolean
     */
    public boolean isConnected(){
        return loggedIn;
    }

    /**
     * Gets the current server address
     * @return String
     */
    public String getAddress(){
        return chost;
    }

    /**
     * Gets the current server port
     * @return int
     */
    public int getPort(){
        return cport;
    }

}
