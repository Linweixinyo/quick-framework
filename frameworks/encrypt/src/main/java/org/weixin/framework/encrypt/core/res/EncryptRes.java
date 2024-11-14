package org.weixin.framework.encrypt.core.res;

import lombok.Data;
import lombok.experimental.Accessors;
import org.weixin.framework.encrypt.core.Encryptable;

@Data
@Accessors(chain = true)
public class EncryptRes implements Encryptable {

    private String data;

}
