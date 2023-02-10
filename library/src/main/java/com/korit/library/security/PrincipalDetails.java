package com.korit.library.security;

import com.korit.library.entity.RoleDtl;
import com.korit.library.entity.RoleMst;
import com.korit.library.entity.UserMst;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.core.user.OAuth2User;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

@RequiredArgsConstructor
@AllArgsConstructor
public class PrincipalDetails implements UserDetails, OAuth2User {

    @Getter
    private final UserMst user;
    private Map<String, Object> response;


    // 시큘리티를 쓸려면 밑에 정보가 전부 필요하다
    // 권한을 리스트로 관리하는 부분
    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        ArrayList<GrantedAuthority> authorities = new ArrayList<GrantedAuthority>();

        List<RoleDtl> roleDtlList = user.getRoleDtl();
        for (int i = 0; i < roleDtlList.size(); i++) {
            RoleDtl dtl = roleDtlList.get(i); // 여기서 가져오는것 하나하나가 RoleDtlDto이다
//            0 = ROLE_USER, 1 = ROLE_ADMIN
            RoleMst roleMst = dtl.getRoleMst();
            String roleName = roleMst.getRoleName();

            GrantedAuthority role = new GrantedAuthority() {
                @Override
                public String getAuthority() {
                    return roleName;
                }
            };
//            System.out.println(roleName == role.getAuthority());
            authorities.add(role);
        }
    //      위의 부분들을 람다로 쓴것이 아래 코드이다.

//        user.getRoleDtlDto().forEach(dtl -> {
//            authorities.add(
//                    () -> {
//                        return dtl.getRoleMstDto().getRoleName();
//                    });
//        });
        // 위의것을 간단하게 쓴게 아래 부분이다.
//        user.getRoleDtlDto().forEach(dtl -> {
//            authorities.add(() -> dtl.getRoleMstDto().getRoleName());
//            // 여기에 람다식을 적음으로 GrantedAuthority 객체를 생성한다
//        });

        return authorities;
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }
    // 여기에 있는 getPassword를 가지고 시큘리티가 비밀번호를 복호화한다

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    // 밑에 4개중에 false가 하나라도 있으면 로그인이 안됨
    /*
        계정 만료 여부 (일정기간이 지낫을때 그 기간 이후에 못쓰게 하는 부분)
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /*
        계정 잠김 여부 (휴먼계정을 의미)
        휴면계정을 여기에 의미를 줘도되고 아래 사용자 활성화 여부에 줘도 된다
     */
    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    /*
        비밀번호 만료 여부 (일정횟수 비밀번호 틀렷을때 비밀번호를 잠구는 경우)
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /*
        사용자 활성화 여부
        (회원가입을 했는데 전화번호 인증이나 이메일 인증을 하지않은 경우 또는 휴먼계정으로 전환된 경우)
     */
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return user.getName();
    }

    @Override
    public Map<String, Object> getAttributes() {
        return response;
    }
}
