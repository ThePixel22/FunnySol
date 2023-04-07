package sol.funny.demobatch.processor;

import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.item.ItemProcessor;
import sol.funny.commonutils.common.CommonKeys;
import sol.funny.commonutils.sercurity.PasswordEncoder;
import sol.funny.datacore.entity.domain.Client;
import sol.funny.demobatch.bean.ClientBean;

import java.util.Date;

@Slf4j
public class ClientItemProcessor implements ItemProcessor<ClientBean, Client> {

    @Override
    public Client process(ClientBean clientBean) throws Exception {
        Client newClient = new Client();
        newClient.setUserName(clientBean.getUserName());
        newClient.setPassword(PasswordEncoder.encode(clientBean.getPassword()));
        newClient.setStatus(CommonKeys.ACTIVE_STATUS);

        newClient.setLastUpdateDate(new Date());

        log.info("Process person " + clientBean.getUserName());
        return newClient;
    }
}
