package goduoel.com.kakaointern.data.error;

public class NoItemException extends Exception{

    public NoItemException() {
        super("검색결과가 없습니다.");
    }
}
