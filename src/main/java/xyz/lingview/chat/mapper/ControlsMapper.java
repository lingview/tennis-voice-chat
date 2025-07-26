package xyz.lingview.chat.mapper;

import xyz.lingview.chat.domain.*;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Mapper
@Repository
public interface ControlsMapper {
    int selectUser(String username);
    int insertUser(Register register);
    Login loginUser(Login login);

    void createChatRoom(ChatRoom chatRoom);

    ChatRoom findChatRoomByUuid(@Param("uuid") String uuid);

    void closeChatRoom(@Param("uuid") String uuid);
    void saveMessage(Message message);
    List<Message> getChatHistory(@Param("chat_room_id") String chat_room_id);

}
