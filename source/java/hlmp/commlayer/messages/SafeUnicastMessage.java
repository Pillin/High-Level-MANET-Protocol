package hlmp.commlayer.messages;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import hlmp.commlayer.NetUser;
import hlmp.commlayer.constants.*;
import hlmp.netlayer.NetHandler;
import hlmp.netlayer.NetMessage;
import hlmp.netlayer.Tools;

public abstract class SafeUnicastMessage extends Message {

	/// <summary>
    /// El usuario receptor de este mensaje
    /// </summary>
    private NetUser targetNetUser;

    /// <summary>
    /// El tiempo de espera restante para reenvíar este mensaje mientras espera el ACK
    /// </summary>
    private int waitTimeOut;

    /// <summary>
    /// Default Constructor
    /// </summary>
    public SafeUnicastMessage()
    {
        this.id = UUID.randomUUID();
        this.jumps = 0;
        this.failReason = MessageFailReason.NOTFAIL;
        this.metaType = MessageMetaType.SAFEUNICAST;
        this.type = MessageType.NOTYPE;
        this.protocolType = MessageProtocolType.NOTYPE;
        this.waitTimeOut = 0;
    }

    /// <summary>
    /// Convierte un paquete de bytes en las propiedades del mensaje
    /// </summary>
    /// <param name="messageData">un array de bytes con todos los datos del mensaje</param>
    @Override
    protected void byteArrayToProperties(byte[] messageData){
        byte[] metaPack = new byte[72];
        byte[] pack = new byte[messageData.length - 72];
        System.arraycopy(messageData, 0, metaPack, 0, metaPack.length);
        System.arraycopy(messageData, 72, pack, 0, pack.length);
        metaUnPack(metaPack);
        unPack(pack);
    }

    public NetUser getTargetNetUser() {
		return targetNetUser;
	}

	public void setTargetNetUser(NetUser targetNetUser) {
		this.targetNetUser = targetNetUser;
	}

	public int getWaitTimeOut() {
		return waitTimeOut;
	}

	public void setWaitTimeOut(int waitTimeOut) {
		this.waitTimeOut = waitTimeOut;
	}
	
	public void WaitTimeOutDec1(){
		this.waitTimeOut--;
	}

    /// <summary>
    /// Convierte a este mensaje en un paquete de bytes
    /// </summary>
    /// <returns>un array de bytes con todos los datos del mensaje</returns>
	@Override
    protected byte[] toByteArray()
    {
        byte[] metaPack = makeMetaPack();
        byte[] pack = makePack();
        byte[] messageData = new byte[metaPack.length + pack.length];
        System.arraycopy(metaPack, 0, messageData, 0, metaPack.length);
        System.arraycopy(pack, 0, messageData, metaPack.length, pack.length);
        return messageData;
    }

    /// <summary>
    /// Envía el mensaje a la MANET
    /// </summary>
    /// <param name="netHandler">El manejador de la red</param>
    /// <param name="ip">la ip de la maquina remota destino</param>
    public boolean send(NetHandler netHandler, InetAddress ip)
    {
        try {
			return netHandler.sendTcpMessage(new NetMessage(toByteArray()), ip);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
    }

    /// <summary>
    /// Convierte la meta data de este mensaje en una estructura de bytes
    /// </summary>
    /// <returns>el array de bytes con la meta data</returns>
    private byte[] makeMetaPack()
    {
    	byte[] messageMetaType = Tools.intToByteArray(this.metaType); //4 (0 - 3)
        byte[] messageType = Tools.intToByteArray(this.type); //4 (4 - 7)
        byte[] messageProtocolType = Tools.intToByteArray(this.protocolType); //4 (8 - 11)
        byte[] userId = Tools.UUIDtoBytes(this.senderNetUser.getId()); //16 (12 - 27)
        byte[] userIp = this.senderNetUser.getIp().getAddress(); //4 (28-31)
        byte[] messageId = Tools.UUIDtoBytes(this.id); //16 (32 - 47)
        byte[] messageJumps = Tools.intToByteArray(this.jumps); //4 (48 - 51)
        byte[] targetId = Tools.UUIDtoBytes(this.targetNetUser.getId()); //16 (52 - 67)
        byte[] targetIp = this.targetNetUser.getIp().getAddress(); //4 (68-71)

        byte[] pack = new byte[72];
        System.arraycopy(messageMetaType, 0, pack, 0, 4);
        System.arraycopy(messageType, 0, pack, 4, 4);
        System.arraycopy(messageProtocolType, 0, pack, 8, 4);
        System.arraycopy(userId, 0, pack, 12, 16);
        System.arraycopy(userIp, 0, pack, 28, 4);
        System.arraycopy(messageId, 0, pack, 32, 16);
        System.arraycopy(messageJumps, 0, pack, 48, 4);
        System.arraycopy(targetId, 0, pack, 52, 16);
        System.arraycopy(targetIp, 0, pack, 68, 4);
        return pack;
    }

    /// <summary>
    /// Convierte una estructura de bytes en la meta data de este mensaje
    /// </summary>
    /// <param name="messageMetaPack">un array de bytes con la meta data</param>
    private void metaUnPack(byte[] messageMetaPack)
    {
    	this.metaType = Tools.readInt(messageMetaPack, 0);
        type = Tools.readInt(messageMetaPack, 4);
        protocolType = Tools.readInt(messageMetaPack, 8);
        senderNetUser = new NetUser();
        byte[] userId = new byte[16];
        System.arraycopy(messageMetaPack, 12, userId, 0, 16);
        senderNetUser.setId(Tools.bytesToUUID(userId));
        byte[] userIP = new byte[4];
        System.arraycopy(messageMetaPack, 28, userIP, 0, 4);
        try {
			senderNetUser.setIp(InetAddress.getByAddress(userIP));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        byte[] messageId = new byte[16];
        System.arraycopy(messageMetaPack, 32, messageId, 0, 16);
        id = Tools.bytesToUUID(messageId);
        jumps = Tools.readInt(messageMetaPack, 48);
        targetNetUser = new NetUser();
        byte[] targetId = new byte[16];
        System.arraycopy(messageMetaPack, 52, targetId, 0, 16);
        targetNetUser.setId(Tools.bytesToUUID(targetId));
        byte[] targetIP = new byte[4];
        System.arraycopy(messageMetaPack, 68, targetIP, 0, 4);
        try {
			targetNetUser.setIp(InetAddress.getByAddress(targetIP));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    }

    /// <summary>
    /// Convierte las propiedades del mensaje en un paquete de bytes
    /// </summary>
    /// <returns>un paquete de bytes con las propiedades del mensaje</returns>
    public abstract byte[] makePack();

    /// <summary>
    /// Convierte un paquete de bytes en las propiedades del mensaje
    /// </summary>
    /// <param name="messagePack">El paquete de bytes</param>
    public abstract void unPack(byte[] messagePack);

    /// <summary>
    /// Sobreescribe el metodo toString
    /// </summary>
    /// <returns>El string que representa este objeto</returns>
    @Override
    public String toString(){
        return "SafeUnicastMessage Id = " + id + " : ";
    }

}
