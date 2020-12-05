package com.github.ep2p.eleuth.service.row;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.github.ep2p.kademlia.connection.ConnectionInfo;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Builder
@ToString
public class ROWConnectionInfo implements ConnectionInfo {
    private static final long serialVersionUID = 4405959614376713329L;
    private String address;
    private int port;
    private boolean ssl;

    @JsonIgnore
    public String getHttpAddress() {
        return "http"+getSslSupportS()+"://"+ address + ":" + port;
    }

    private String getSslSupportS() {
        return ssl ? "s" : "";
    }

    @JsonIgnore
    public String getFullAddress(){
        return "ws"+getSslSupportS()+"://"+address+":"+port+"/ws";
    }

}
