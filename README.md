# simple-ha
参考flink HA模块，实现了一套简易HA框架，目前只支持了kubernetes native。

## 前置条件
- 部署kubernetes集群
- 以反向代理的模式运行 kubectl（可选，仅用于本地测试）  
`kubectl proxy --port=8888 &`

## Example code
basic usage
```java
  String masterUrl = "localhost:8888";
  String clusterId = "my-cluster";
  String serverName = "my-server";
  String serverAddr = "localhost:8080";
  KubernetesHaConfig haConfig = KubernetesHaConfig.builder()
          .withMasterUrl(masterUrl)  // just for local test
          .withClusterId(clusterId)
          .withServerName(serverName)
          .withServerAddress(serverAddr).build();
  KubernetesHaServices services = new KubernetesHaServices(haConfig);
  services.start(new DefaultContender(), true);
  // wait election
  Thread.sleep(1000);
  // asserts
  assert services.isLeading();
  PodInformation leaderInfo = services.getLeaderInformation();
  assert serverName.equals(leaderInfo.getName());
  assert serverAddr.equals(leaderInfo.getAddress());
```

advanced usage
```java
  KubernetesHaConfig haConfig = KubernetesHaConfig.builder()
            .withMasterUrl(masterUrl)  // just for local test
            .withClusterId(clusterId)
            .withServerName(serverName)
            .withServerAddress(serverAddr).build();
  KubernetesHaServices services = new KubernetesHaServices(haConfig);
  services.start(new Contender() {
      @Override
      public void leaderChange(PodInformation newPod) {
          System.out.println("handle leader change, new leader: " + newPod);
      }

      @Override
      public void handleError(Exception exception) {
          System.out.println("handle error");
          exception.printStackTrace();
      }

      @Override
      public void grantLeadership() {
          System.out.println("I am leader now!");
      }

      @Override
      public void revokeLeadership() {
          System.out.println("I am not leader any more.");
      }
  }, true);
```
