package top.dumbzarro.template.common.helper.jwt;

import top.dumbzarro.template.common.biz.BizClaims;

public interface JwtHelper {

    String generateToken(String subject, BizClaims claims);

    BizClaims parseToken(String token);
}
