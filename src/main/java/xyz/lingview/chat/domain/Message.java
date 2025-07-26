package xyz.lingview.chat.domain;

import lombok.Data;
import java.util.Date;

@Data
public class Message {
    private Integer id;
    private String chat_room_id;
    private String username;
    private String chat;
    private Date create_time;
    private int status;
}

