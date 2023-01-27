package com.korit.library.security;

import com.korit.library.aop.annotation.ParamsAspect;
import com.korit.library.entity.UserMst;
import com.korit.library.repository.AccountRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class PrincipalDetailsService implements UserDetailsService {

    @Autowired
    private AccountRepository accountRepository;

    @ParamsAspect
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // 해당 username이 DB(user_mst table)에 존재하는지 확인!
        UserMst userMst = accountRepository.findUserByUsername(username);
        // 데이터베이스에 해당하는 username을 찾아온다

        if (userMst == null) {
            throw new UsernameNotFoundException("회원정보를 확인 할 수 없음");
            // 이 예외가 밀려나면 위에 UsernameNotFoundException 로 미룬다
        }

        log.info("로그인 시도 요청 들어옴?");

        return new PrincipalDetails(userMst);
    }
}
