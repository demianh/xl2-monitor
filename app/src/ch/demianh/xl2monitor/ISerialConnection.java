package ch.demianh.xl2monitor;

interface ISerialConnection {

    void disconnect();

    boolean isConnected();

    String queryString(String query) throws SerialConnectionException;

    byte[] queryBytes(String query) throws SerialConnectionException;
}
