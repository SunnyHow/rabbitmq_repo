package com.ehualu.stdp.rabbitmq.demo.security;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

/**
 * @ProjectName: stdp-security-demo
 * @Package: com.ehualu.stdp.security
 * @ClassName: LoginUtil
 * @Author: stdp
 * @Description: ${description}
 * @Date: 2019/12/20 14:29
 * @Version: 1.0
 */
public class LoginUtil {
    public enum Module {
        STORM("StormClient"), KAFKA("KafkaClient"), ZOOKEEPER("Client");

        private String name;

        private Module(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }
    }

    private static final Logger LOGGER = LoggerFactory.getLogger(LoginUtil.class);

    /**
     * line operator string
     */
    private static final String LINE_SEPARATOR = System.getProperty("line.separator");

    /**
     * jaas file postfix
     */
    private static final String JAAS_POSTFIX = ".jaas.conf";

    private static final String JAVA_SECURITY_KRB5_CONF_KEY = "java.security.krb5.conf";

    public static final String JAVA_SECURITY_LOGIN_CONF_KEY = "java.security.auth.login.config";

    private static final String ZOOKEEPER_SERVER_PRINCIPAL_KEY = "zookeeper.server.principal";

    private static final String LOGIN_FAILED_CAUSE_PASSWORD_WRONG = "(wrong password) keytab file and user not match, you can kinit -k -t keytab user in client server to check";

    private static final String LOGIN_FAILED_CAUSE_TIME_WRONG = "(clock skew) time of local server and remote server not match, please check ntp to remote server";

    private static final String LOGIN_FAILED_CAUSE_AES256_WRONG = "(aes256 not support) aes256 not support by default jdk/jre, need copy local_policy.jar and US_export_policy.jar from remote server in path /opt/huawei/Bigdata/jdk/jre/lib/security";

    private static final String LOGIN_FAILED_CAUSE_PRINCIPAL_WRONG = "(no rule) principal format not support by default, need add property hadoop.security.auth_to_local(in core-site.xml) value RULE:[1:$1] RULE:[2:$1]";

    private static final String LOGIN_FAILED_CAUSE_TIME_OUT = "(time out) can not connect to kdc server or there is fire wall in the network";

    private static final boolean IS_IBM_JDK = System.getProperty("java.vendor").contains("IBM");

    /**
     * IBM jdk login module
     */
    private static final String IBM_LOGIN_MODULE = "com.ibm.security.auth.module.Krb5LoginModule required";

    /**
     * oracle jdk login module
     */
    private static final String SUN_LOGIN_MODULE = "com.sun.security.auth.module.Krb5LoginModule required";


    public synchronized static void login(String userPrincipal, String userKeytabPath, String krb5ConfPath)
            throws IOException
    {
        // 1.check input parameters
        if ((userPrincipal == null) || (userPrincipal.length() <= 0))
        {
            LOGGER.error("input userPrincipal is invalid.");
            throw new IOException("input userPrincipal is invalid.");
        }

        if ((userKeytabPath == null) || (userKeytabPath.length() <= 0))
        {
            LOGGER.error("input userKeytabPath is invalid.");
            throw new IOException("input userKeytabPath is invalid.");
        }

        if ((krb5ConfPath == null) || (krb5ConfPath.length() <= 0))
        {
            LOGGER.error("input krb5ConfPath is invalid.");
            throw new IOException("input krb5ConfPath is invalid.");
        }

        // 2.check file exsits
        File userKeytabFile = new File(userKeytabPath);
        if (!userKeytabFile.exists())
        {
            LOGGER.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
            throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") does not exsit.");
        }
        if (!userKeytabFile.isFile())
        {
            LOGGER.error("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
            throw new IOException("userKeytabFile(" + userKeytabFile.getAbsolutePath() + ") is not a file.");
        }

        File krb5ConfFile = new File(krb5ConfPath);
        if (!krb5ConfFile.exists())
        {
            LOGGER.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
            throw new IOException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") does not exsit.");
        }
        if (!krb5ConfFile.isFile())
        {
            LOGGER.error("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
            throw new IOException("krb5ConfFile(" + krb5ConfFile.getAbsolutePath() + ") is not a file.");
        }

        // 3.set and check krb5config
        setKrb5Config(krb5ConfFile.getAbsolutePath());

//        LOGGER.info("check zookeeper server Principal =============================================");
        setZookeeperServerPrincipal(userPrincipal);
//        LOGGER.info("check jaas.conf +++++++++++++++++++++++++++++++++++++++++++++++++");
        setJaasFile(userPrincipal,userKeytabPath);
        LOGGER.info("Login success!!!!!!!!!!!!!!");
    }


    public static void setKrb5Config(String krb5ConfigFile) throws IOException {
        System.setProperty(JAVA_SECURITY_KRB5_CONF_KEY,krb5ConfigFile);
        String ret = System.getProperty(JAVA_SECURITY_KRB5_CONF_KEY);
        if (ret == null) {
            LOGGER.error(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
            throw new IOException(JAVA_SECURITY_KRB5_CONF_KEY + " is null.");
        }
        if (!ret.equals(krb5ConfigFile)){
            LOGGER.error(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfigFile + ".");
            throw new IOException(JAVA_SECURITY_KRB5_CONF_KEY + " is " + ret + " is not " + krb5ConfigFile + ".");
        }
    }

//    public static void setJaasFile(String jaasPath) throws IOException {
//        System.setProperty(JAVA_SECURITY_LOGIN_CONF_KEY,jaasPath);
//        String ret = System.getProperty(JAVA_SECURITY_LOGIN_CONF_KEY);
//        if (ret == null) {
//            LOGGER.error(JAVA_SECURITY_LOGIN_CONF_KEY + " is null.");
//            throw new IOException(JAVA_SECURITY_LOGIN_CONF_KEY + " is null.");
//        }
//        if (!ret.equals(jaasPath)){
//            LOGGER.error(JAVA_SECURITY_LOGIN_CONF_KEY + " is " + ret + " is not " + jaasPath + ".");
//            throw new IOException(JAVA_SECURITY_LOGIN_CONF_KEY + " is " + ret + " is not " + jaasPath + ".");
//        }
//    }

    public static void setJaasFile(String userPrincipal,String userKeytabPath) throws IOException {
        String jaasPath = new File(System.getProperty("java.io.tmpdir")) + File.separator + System.getProperty("user.name") + JAAS_POSTFIX;
        LOGGER.info("jaasPath = {}",jaasPath);
        //windows路径下分隔符替换
        jaasPath = jaasPath.replace("\\","\\\\");
        userKeytabPath = userKeytabPath.replace("\\","\\\\");
        //删除jaas文件
        deleteJaasFile(jaasPath);
        writeJaasFile(jaasPath,userPrincipal,userKeytabPath);
        System.setProperty(JAVA_SECURITY_LOGIN_CONF_KEY,jaasPath);
    }

    private static void deleteJaasFile(String jaasPath) throws IOException {
        File jaasFile = new File(jaasPath);
        if (jaasFile.exists()){
            if (!jaasFile.delete()){
                throw new IOException("failed to delete exists jaas file.");
            }
        }
    }

    private static void writeJaasFile(String jaasPath,String userPrincipal,String userKeytabPath) throws IOException {
        FileWriter writer = new FileWriter(new File(jaasPath));
        try{
            writer.write(getJaasConfContext(userPrincipal,userKeytabPath));
            writer.flush();
        }catch (IOException e){
            throw new IOException("Failed to create jaas.conf File.");
        }finally {
            writer.close();
        }
    }


    private static String getJaasConfContext(String userPrincipal,String userKeytabPath) throws IOException{
        Module[] allModule = Module.values();
        StringBuffer builder = new StringBuffer();
        for (Module module: allModule){
            String serviceName = null;
            if ("Client".equals(module.getName())){
                serviceName = "zookeeper";
            }else if ("KafkaClient".equals(module.getName())){
                serviceName = "kafka";
            }else {
                serviceName = "storm";
            }
            builder.append(getModuleContext(userPrincipal,userKeytabPath,module,serviceName));
        }
        return builder.toString();
    }

    private static String getModuleContext(String userPrincipal,String userKeytabPath,Module module,String serviceName) throws IOException {
        StringBuffer builder = new StringBuffer();
        if (IS_IBM_JDK){
            builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
            builder.append("credsType=both").append(LINE_SEPARATOR);
            builder.append("principal=\"" + userPrincipal + "\"").append(LINE_SEPARATOR);
            builder.append("useKeytab=\"" + userKeytabPath + "\"").append(LINE_SEPARATOR);
            builder.append("serviceName=\""+serviceName + "\"").append(LINE_SEPARATOR);
            builder.append("debug=true;").append(LINE_SEPARATOR);
            builder.append("};").append(LINE_SEPARATOR);
        }else {
            builder.append(module.getName()).append(" {").append(LINE_SEPARATOR);
            builder.append(SUN_LOGIN_MODULE).append(LINE_SEPARATOR);
            builder.append("useKeyTab=true").append(LINE_SEPARATOR);
            builder.append("keyTab=\"" + userKeytabPath + "\"").append(LINE_SEPARATOR);
            builder.append("principal=\"" + userPrincipal + "\"").append(LINE_SEPARATOR);
            builder.append("serviceName=\""+serviceName + "\"").append(LINE_SEPARATOR);
            builder.append("useTicketCache=false").append(LINE_SEPARATOR);
            builder.append("storeKey=true").append(LINE_SEPARATOR);
            builder.append("debug=true;").append(LINE_SEPARATOR);
            builder.append("};").append(LINE_SEPARATOR);
        }
        return builder.toString();
    }


    public static void setZookeeperServerPrincipal(String zkServerPrincipal) throws IOException {
        System.setProperty(ZOOKEEPER_SERVER_PRINCIPAL_KEY,zkServerPrincipal);
        String ret = System.getProperty(ZOOKEEPER_SERVER_PRINCIPAL_KEY);
        if (ret == null) {
            LOGGER.error(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is null.");
            throw new IOException(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is null.");
        }
        if (!ret.equals(zkServerPrincipal)){
            LOGGER.error(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is " + ret + " is not " + zkServerPrincipal + ".");
            throw new IOException(ZOOKEEPER_SERVER_PRINCIPAL_KEY + " is " + ret + " is not " + zkServerPrincipal + ".");
        }
    }
}
