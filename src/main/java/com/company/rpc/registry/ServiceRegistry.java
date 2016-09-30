package com.company.rpc.registry;

import java.io.IOException;
import java.util.concurrent.CountDownLatch;

import org.apache.log4j.Logger;
import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.ZooDefs;
import org.apache.zookeeper.ZooKeeper;

import com.company.rpc.conf.Constant;

public class ServiceRegistry {

    private static final Logger LOGGER = Logger.getLogger(ServiceRegistry.class);

    private CountDownLatch latch = new CountDownLatch(1);

    private String registryAddress;

    public ServiceRegistry(String registryAddress) {
        this.registryAddress = registryAddress;
    }

    public void register(String data) {
        if (data != null) {
            ZooKeeper zk = connectServer();
            if (zk != null) {
                createNode(zk, data);
            }
        }
    }
    
    /**
     * 是否存在path路径节点
     * 
     * @param path
     * @return
     */
    public boolean exists(ZooKeeper zk, String path) {
        try {
            return zk.exists(path, false) != null;
        } catch (Exception e) {
        	LOGGER.error("", e);
        }
        return false;
    }

    private ZooKeeper connectServer() {
        ZooKeeper zk = null;
        try {
            zk = new ZooKeeper(registryAddress, Constant.ZK_SESSION_TIMEOUT, new Watcher() {
                @Override
                public void process(WatchedEvent event) {
                    if (event.getState() == Event.KeeperState.SyncConnected) {
                        latch.countDown();
                    }
                }
            });
            latch.await();
        } catch (IOException | InterruptedException e) {
            LOGGER.error("", e);
        }
        return zk;
    }

    private void createNode(ZooKeeper zk, String data) {
        try {
        	// 如果'/registry'节点不存在，先创建'/registry'
        	if (!exists(zk, Constant.ZK_REGISTRY_PATH)) {
        		String regPath = zk.create(Constant.ZK_REGISTRY_PATH, "".getBytes(), ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
        		LOGGER.debug("create zookeeper node ({" + regPath + "}");
        	}
            byte[] bytes = data.getBytes();
            String path = zk.create(Constant.ZK_DATA_PATH, bytes, ZooDefs.Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL_SEQUENTIAL);
            LOGGER.debug("create zookeeper node ({" + path + "} => {" + data + "})");
        } catch (KeeperException | InterruptedException e) {
            LOGGER.error("", e);
        }
    }
}
