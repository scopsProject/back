package com.example.projectNameBack.dto;
import com.example.projectNameBack.entity.SongSession;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SongSessionDto {
    private String sessionType;
    private String playerName;

    public static SongSessionDto from(SongSession session) {
        String playerName = "알 수 없음(탈퇴)"; // 기본값

        if (session.getPlayer() != null) {
            playerName = session.getPlayer().getUserName();
        }

        return new SongSessionDto(
                session.getSessionType(),
                playerName
        );
    }
}
