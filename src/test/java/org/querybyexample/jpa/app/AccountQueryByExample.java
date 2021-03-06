/*
 *  Copyright 2012 JAXIO http://www.jaxio.com
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.querybyexample.jpa.app;

import javax.inject.Named;
import javax.inject.Singleton;

import org.querybyexample.jpa.QueryByExample;

/**
 * JPA 2 Query By Example for {@link Account}.
 */
@Named
@Singleton
public class AccountQueryByExample extends QueryByExample<Account, String> {
    public AccountQueryByExample() {
        super(Account.class);
    }

}