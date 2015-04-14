 /*
  * Copyright 2015 Couchbase, Inc.
  *
  * Licensed under the Apache License, Version 2.0 (the "License");
  * you may not use this file except in compliance with the License.
  * You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */

package com.couchbase.example.servlet.conn;

import com.couchbase.client.java.Cluster;
import com.couchbase.client.java.CouchbaseCluster;
import com.couchbase.example.servlet.cfg.ConfigManager;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author David Maier <david.maier at couchbase.com>
 */
public class ClusterFactory {
    
    private static Cluster cluster;
    
    
    public static Cluster getCluster()
    {
        if (cluster == null)
            createCluster();
       
        return cluster;
    }
    
    public static Cluster createCluster()
    {
        String[] hosts = ConfigManager.getCBConfig().getHosts();
        
        List<String> nodes = Arrays.asList(hosts);
      
        cluster = CouchbaseCluster.create(nodes); 
        
        return cluster;
    }
}
