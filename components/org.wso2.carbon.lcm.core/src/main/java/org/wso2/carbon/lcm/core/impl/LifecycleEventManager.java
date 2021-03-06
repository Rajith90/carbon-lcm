/*
 * Copyright (c) 2017, WSO2 Inc. (http://www.wso2.org) All Rights Reserved.
 *
 * WSO2 Inc. licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */

package org.wso2.carbon.lcm.core.impl;

import org.wso2.carbon.lcm.core.exception.LifecycleException;
import org.wso2.carbon.lcm.sql.beans.LifecycleHistoryBean;
import org.wso2.carbon.lcm.sql.beans.LifecycleStateBean;
import org.wso2.carbon.lcm.sql.dao.LifecycleMgtDAO;
import org.wso2.carbon.lcm.sql.exception.LifecycleManagerDatabaseException;

import java.util.List;

/**
 * This class communicate with DAO layer to perform lifecycle operations.
 */
public class LifecycleEventManager {

    /**
     * Associates lifecycle with an asset. Sets the initial state as the current state.
     *
     * @param lcName                        Name of lifecycle which asset being associated with.
     * @param initialState                  initial state provided in the scxml configuration.
     * @param user                          The user who invoked the action. This will be used for auditing purposes.
     * @return                              uuid generated for that particular asset.
     * @throws LifecycleException           If failed associate lifecycle state.
     */
    public String associateLifecycle(String lcName, String initialState, String user) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().addLifecycleState(initialState, lcName, user);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while associating lifecycle " + lcName, e);
        }
    }

    /**
     * Associates lifecycle with an asset. Sets the initial state as the current state.
     *
     * @param lcName       Name of lifecycle which asset being associated with.
     * @param lifecycleId  Lifecycle ID
     * @param initialState initial state provided in the scxml configuration.
     * @param user         The user who invoked the action. This will be used for auditing purposes.
     * @throws LifecycleException  If failed associate lifecycle state.
     */
    public void associateLifecycle(String lcName, String lifecycleId, String initialState, String user) throws
            LifecycleException {
        try {
            getLCMgtDAOInstance().addLifecycleState(initialState, lifecycleId, lcName, user);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while associating lifecycle " + lcName, e);
        }
    }

    /**
     * Changes the lifecycle state.
     *
     * @param currentState                  Current state
     * @param requiredState                 The expected state
     * @param id                            uuid of the current state which maps with the asset.
     * @param user                          The user who invoked the action. This will be used for auditing purposes.
     * @throws LifecycleException           If failed to change lifecycle state.
     */
    public void changeLifecycleState(String currentState, String requiredState, String id, String user)
            throws LifecycleException {
        LifecycleStateBean lifecycleStateBean = new LifecycleStateBean();
        lifecycleStateBean.setPreviousStatus(currentState);
        lifecycleStateBean.setPostStatus(requiredState);
        lifecycleStateBean.setStateId(id);
        try {
            getLCMgtDAOInstance().changeLifecycleState(lifecycleStateBean, user);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while changing lifecycle state to  " + requiredState, e);
        }
    }

    /**
     * Remove lifecycle state data from LC_DATA table
     *
     * @param uuid                        uuid of the state.
     * @throws LifecycleException         If failed to remove lifecycle data.
     */
    public void removeLifecycleStateData(String uuid) throws LifecycleException {
        try {
            getLCMgtDAOInstance().removeLifecycleState(uuid);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while deleting lifecycle data for id : " + uuid);
        }
    }

    /**
     * Get data related to particular uuid from LC_DATA table
     *
     * @param uuid                        uuid of the state.
     * @return                            Lifecycle state data associated with the uuid.
     * @throws LifecycleException         If failed to get lifecycle state data.
     */
    public LifecycleStateBean getLifecycleStateData(String uuid) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().getLifecycleStateDataFromId(uuid);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle data for id : " + uuid);
        }
    }

    /**
     * Get data related to particular uuid and state from LC_CHECKLIST_DATA table
     *
     * @param uuid                        uuid of the state.
     * @param lcState                     State which data is required.
     * @return                            Lifecycle state data associated with the uuid.
     * @throws LifecycleException         If failed to get lifecycle state data.
     */
    public LifecycleStateBean getLifecycleDataFromState(String uuid, String lcState) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().getLifecycleCheckListDataFromState(uuid, lcState);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle data for id : " + uuid);
        }
    }

    public void changeCheckListItemData(String uuid, String currentState, String checkListItemName, boolean value)
            throws LifecycleException {
        try {
            getLCMgtDAOInstance().changeCheckListItemData(uuid, currentState, checkListItemName, value);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while adding checklist data for item " + checkListItemName, e);
        }
    }

    /**
     * This method provides set of operations performed to a particular lifecycle id.
     *
     * @param uuid                  Lifecycle Id which requires history.
     * @return                      List of lifecycle history objects.
     * @throws LifecycleException   If failed to get lifecycle history from ID.
     */
    public List<LifecycleHistoryBean> getLifecycleHistoryFromId(String uuid) throws LifecycleException {
        try {
           return getLCMgtDAOInstance().getLifecycleHistoryFromId(uuid);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle data from uuid : " + uuid, e);
        }
    }

    /**
     * This method provides set of lifecycle ids in a particular state.
     *
     * @param state                 Filtering state.
     * @param lcName                Name of the relevant lifecycle.
     * @return                      {@code List<LifecycleHistoryBean>} List of lifecycle ids in the given state.
     * @throws LifecycleException   If failed to get lifecycle Ids
     */
    public List<String> getLifecycleIds (String state, String lcName) throws LifecycleException {
        try {
            return getLCMgtDAOInstance().getLifecycleIdsFromState(state, lcName);
        } catch (LifecycleManagerDatabaseException e) {
            throw new LifecycleException("Error while getting lifecycle ids in state : " + state, e);
        }
    }

    private LifecycleMgtDAO getLCMgtDAOInstance() {
        return LifecycleMgtDAO.getInstance();
    }
}
