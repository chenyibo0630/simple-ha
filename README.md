# simple-ha
参考flink HA模块，实现了一套简易HA框架，目前只支持了kubernetes native。

## 前置条件
- 部署kubernetes集群
- 以反向代理的模式运行 kubectl（可选，仅用于本地测试）  
`kubectl proxy --port=8888 &`

## Example code
```java
  KubernetesHaConfig haConfig = KubernetesHaConfig.builder()
    .withMasterUrl("localhost:8888") // 用于本地测试
    .withClusterId("my-cluster")
    .withServerName("my-server")
    .withServerAddress("localhost:8080").build();
  KubernetesHaServices services = new KubernetesHaServices(haConfig);
  services.start(new DefaultContender(), true);
```
