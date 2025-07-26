// ChatRoom.java
package xyz.lingview.chat.domain;

import lombok.Data;

import java.util.Date;

@Data
public class ChatRoom {
    private String uuid;
    private String create_user;
    private int status;
    private Date create_time;

}
