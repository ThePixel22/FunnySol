package sol.funny.demobatch.bean;

import lombok.Data;

@Data
public class ClientBean {
    private String userName;

    private String password;

    public ClientBean(String userName, String password) {
        this.userName = userName;
        this.password = password;
    }

    public ClientBean() {
    }
}
