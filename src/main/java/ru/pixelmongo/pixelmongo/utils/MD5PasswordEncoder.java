package ru.pixelmongo.pixelmongo.utils;

import org.springframework.security.crypto.password.PasswordEncoder;

public class MD5PasswordEncoder implements PasswordEncoder{
    
    private final int times;
    
    public MD5PasswordEncoder(int times) {
        this.times = times;
    }
    
    @Override
    public String encode(CharSequence rawPassword) {
        return MD5EncodeUtils.md5(rawPassword, times);
    }

    @Override
    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        return encode(rawPassword).equals(encodedPassword);
    }

}
