/*
 * Copyright 2016 Benjamin Sautner
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 */

package com.nimbits.server.process.cron;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.task.TaskExecutor;
import org.springframework.stereotype.Component;

import java.util.logging.Logger;

@Component
public class SystemTaskExecutor {
    private final Logger logger = Logger.getLogger(SystemTaskExecutor.class.getName());



    @Autowired
    private TaskExecutor taskExecutor;
    @Autowired
    private SystemCron systemCron;




    public SystemTaskExecutor() {


    }

    private class SystemTask implements Runnable {




        public SystemTask() {

        }

        public void run() {

            try {

                systemCron.process();


            } catch (Exception e) {
                logger.severe(e.getMessage());
            }

        }

    }



    public void heartbeat() {

        taskExecutor.execute(new SystemTask());

    }

}
