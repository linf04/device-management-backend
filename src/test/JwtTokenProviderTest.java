import com.device.management.security.JwtTokenProvider;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

// 只加载JwtTokenProvider，不启动整个项目
@SpringBootTest(classes = JwtTokenProvider.class)
// 注入测试用的jwt.secret，解决null问题
@TestPropertySource(properties = {"jwt.secret=test-secret-1234567890abcdef1234567890abcdef"})
public class JwtTokenProviderTest {

    // Spring自动注入（会触发@Value赋值），不要手动new！
    @Autowired
    private JwtTokenProvider jwtTokenProvider;

    // 用@Test标记测试方法，而非main方法
    @Test
   public  void testGenerateToken() {
        // 此时jwtSecret已被Spring注入，不会为null
        String token = jwtTokenProvider.generateToken("JS2115");
        System.out.println("生成的Token：" + token);

        String creater = jwtTokenProvider.getUserIdFromToken(token);
        System.out.println("解析的creater：" + creater);
    }
}