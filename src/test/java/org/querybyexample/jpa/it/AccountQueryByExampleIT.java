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
package org.querybyexample.jpa.it;

import static org.fest.assertions.Assertions.assertThat;
import static org.querybyexample.jpa.EntitySelector.newEntitySelector;
import static org.querybyexample.jpa.OrderByDirection.ASC;
import static org.querybyexample.jpa.OrderByDirection.DESC;
import static org.querybyexample.jpa.PropertySelector.newPropertySelector;
import static org.querybyexample.jpa.Ranges.RangeDate.newFromRangeDate;
import static org.querybyexample.jpa.Ranges.RangeDate.newRangeDate;
import static org.querybyexample.jpa.Ranges.RangeDate.newToRangeDate;
import static org.querybyexample.jpa.SearchMode.ANYWHERE;
import static org.querybyexample.jpa.SearchMode.ENDING_LIKE;
import static org.querybyexample.jpa.SearchMode.STARTING_LIKE;
import static org.querybyexample.jpa.app.Account_.addressId;
import static org.querybyexample.jpa.app.Account_.birthDate;
import static org.querybyexample.jpa.app.Account_.homeAddress;
import static org.querybyexample.jpa.app.Account_.username;

import java.text.DateFormat;
import java.text.ParseException;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.querybyexample.jpa.OrderBy;
import org.querybyexample.jpa.OrderByDirection;
import org.querybyexample.jpa.SearchParameters;
import org.querybyexample.jpa.app.Account;
import org.querybyexample.jpa.app.AccountQueryByExample;
import org.querybyexample.jpa.app.Address;
import org.querybyexample.jpa.app.Role;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Integration test illustrating the use of JPA Query By Example project.
 */
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(locations = { "classpath*:applicationContext-test.xml" })
@Transactional
public class AccountQueryByExampleIT {
	@PersistenceContext
	private EntityManager entityManager;

	@Inject
	private AccountQueryByExample accountQBE;

	@Test
	@Rollback
	public void all() {
		assertThat(accountQBE.find(new Account(), new SearchParameters())).hasSize(53);
	}

	@Test
	@Rollback
	public void username() {
		Account admin = new Account();
		admin.setUsername("admin");
		assertThat(accountQBE.find(admin, new SearchParameters())).hasSize(1);

		Account noMatch = new Account();
		noMatch.setUsername("noMatch");
		assertThat(accountQBE.find(noMatch, new SearchParameters())).isEmpty();
	}

	@Test
	@Rollback
	public void usernameAndEmail() {
		Account example = new Account();
		example.setUsername("admin");
		example.setEmail("admin@example.com");
		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(1);

		Account noMatch = new Account();
		noMatch.setUsername("admin");
		noMatch.setEmail("noMatch");
		assertThat(accountQBE.find(noMatch, new SearchParameters())).isEmpty();
	}

	@Test
	@Rollback
	public void usernameStartingLikeAdm() {
		Account example = new Account();
		example.setUsername("adm");
		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(0);
		assertThat(accountQBE.find(example, new SearchParameters().startingLike())).hasSize(1);
		assertThat(accountQBE.find(example, new SearchParameters(STARTING_LIKE))).hasSize(1);
	}

	@Test
	@Rollback
	public void usernameEndingLikeMin() {
		Account example = new Account();
		example.setUsername("min");
		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(0);
		assertThat(accountQBE.find(example, new SearchParameters().endingLike())).hasSize(1);
		assertThat(accountQBE.find(example, new SearchParameters(ENDING_LIKE))).hasSize(1);
	}

	@Test
	@Rollback
	public void usernameContainingMin() {
		Account example = new Account();
		example.setUsername("mi");
		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(0);
		assertThat(accountQBE.find(example, new SearchParameters().anywhere())).hasSize(1);
		assertThat(accountQBE.find(example, new SearchParameters(ANYWHERE))).hasSize(1);
	}

	@Test
	@Rollback
	public void usernameEqualsAdminCaseSensitive() {
		Account example = new Account();
		example.setUsername("AdMiN");
		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(1);
		assertThat(accountQBE.find(example, new SearchParameters().caseSensitive())).hasSize(0);
		assertThat(accountQBE.find(example, new SearchParameters().caseInsensitive())).hasSize(1);
	}

	@Test
	@Rollback
	public void byPropertySelector() {
		assertThat(accountQBE.find(new Account(), new SearchParameters(newPropertySelector(username)))).hasSize(53);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newPropertySelector(username, "demo", "demo")))).hasSize(1);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newPropertySelector(username, "demo", "admin")))).hasSize(2);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newPropertySelector(username, "unknown", "admin")))).hasSize(1);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newPropertySelector(username, "unknown", "invalid")))).isEmpty();
	}

	@Test
	@Rollback
	public void byDateRangeSelectorBetween() throws ParseException {
		assertThat(accountQBE.find(new Account(), new SearchParameters(newRangeDate(birthDate, getDate("01/01/1970"), getDate("01/01/2000"))))).hasSize(2);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newRangeDate(birthDate, getDate("01/01/1988"), getDate("01/01/1989"))))).isEmpty();
	}

	@Test
	@Rollback
	public void byDateRangeSelectorFrom() throws ParseException {
		assertThat(accountQBE.find(new Account(), new SearchParameters(newFromRangeDate(birthDate, getDate("01/01/1970"))))).hasSize(2);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newFromRangeDate(birthDate, getDate("01/01/2100"))))).isEmpty();

	}

	@Test
	@Rollback
	public void byDateRangeSelectorTo() throws ParseException {
		assertThat(accountQBE.find(new Account(), new SearchParameters(newToRangeDate(birthDate, getDate("01/01/1970"))))).isEmpty();
		assertThat(accountQBE.find(new Account(), new SearchParameters(newToRangeDate(birthDate, getDate("01/01/2100"))))).hasSize(2);
	}

	@Test
	@Rollback
	public void byEntitySelector() throws ParseException {
		assertThat(accountQBE.find(new Account(), new SearchParameters(newRangeDate(birthDate, getDate("01/01/1970"), getDate("01/01/2000"))))).hasSize(2);
		assertThat(accountQBE.find(new Account(), new SearchParameters(newRangeDate(birthDate, getDate("01/01/1988"), getDate("01/01/1989"))))).isEmpty();
	}

	@Test
	@Rollback
	public void byEntitySelectors() {
		assertThat(
				accountQBE.find(new Account(),
						new SearchParameters(newEntitySelector(addressId, entityManager.find(Address.class, 1), entityManager.find(Address.class, 2)))))
				.hasSize(2);
	}

	@Test
	@Rollback
	public void leftJoinHomeAddress() {
		assertThat(accountQBE.find(new Account(), new SearchParameters().leftJoin(homeAddress))).hasSize(53);
	}

	@Test
	@Rollback
	public void byManyToOneProperty() throws ParseException {
		Account example = new Account();
		example.setHomeAddress(new Address());
		example.getHomeAddress().setCity("Paris");

		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(1);
	}

	@Test
	@Rollback
	public void byManyToOnePropertyEndingLike() throws ParseException {
		Account example = new Account();
		example.setHomeAddress(new Address());
		example.getHomeAddress().setCity("ris");

		assertThat(accountQBE.find(example, new SearchParameters().endingLike())).hasSize(1);
	}

	@Test
	@Rollback
	public void byManyToMany() {
		Account example = new Account();
		example.addRole(entityManager.find(Role.class, 1));
		example.addRole(entityManager.find(Role.class, 2));

		assertThat(accountQBE.find(example, new SearchParameters())).hasSize(4);
	}

	@Test
	@Rollback
	public void orderByFieldname() {
		List<Account> accounts = accountQBE.find(new Account(), new SearchParameters().orderBy("username"));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("admin");

		accounts = accountQBE.find(new Account(), new SearchParameters().orderBy("username", ASC));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("admin");

		accounts = accountQBE.find(new Account(), new SearchParameters().orderBy("username", DESC));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("user50");

		accounts = accountQBE.find(new Account(), new SearchParameters().orderBy(new OrderBy("username", DESC)));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("user50");
	}

	@Test
	@Rollback
	public void orderByAttribute() {
		List<Account> accounts = accountQBE.find(new Account(), new SearchParameters().orderBy(username));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("admin");

		accounts = accountQBE.find(new Account(), new SearchParameters().orderBy(username, ASC));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("admin");

		accounts = accountQBE.find(new Account(), new SearchParameters().orderBy(username, DESC));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("user50");

		accounts = accountQBE.find(new Account(), new SearchParameters().orderBy(new OrderBy(username, DESC)));
		assertThat(accounts.iterator().next().getUsername()).isEqualTo("user50");
	}

	@Test
	@Rollback
	public void orderByDesc() {
		List<Account> list = accountQBE.find(new Account(), new SearchParameters().orderBy("username", OrderByDirection.DESC));
		assertThat(list.iterator().next().getUsername()).isEqualTo("user50");
	}

	@Test
	@Rollback
	public void bySearchPattern() {
		assertThat(accountQBE.find(new Account(), new SearchParameters().searchPattern("admin"))).hasSize(1);
		assertThat(accountQBE.find(new Account(), new SearchParameters().searchPattern("min").anywhere())).hasSize(1);
		assertThat(accountQBE.find(new Account(), new SearchParameters().searchPattern("no_match").anywhere())).isEmpty();
	}

	@Test
	@Rollback
	public void maxResults() {
		assertThat(accountQBE.find(new Account(), new SearchParameters().maxResults(7))).hasSize(7);
	}

	private Date getDate(String from) throws ParseException {
		return DateFormat.getDateInstance(DateFormat.SHORT, Locale.FRANCE).parse(from);
	}
}