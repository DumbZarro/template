package top.dumbzarro.template.controller.auth.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class AuthResponse {
    private Long userId;
    private String email;
    private String nickname;
    private String avatarUrl;
    private String accountStatus;
    private String token;
} 