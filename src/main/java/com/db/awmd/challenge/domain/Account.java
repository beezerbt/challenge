package com.db.awmd.challenge.domain;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Account {

  @NotNull
  @NotEmpty
  private final String accountId;

  @NotNull
  @Min(value = 0, message = "Initial balance must be positive.")
  private BigDecimal balance;

  public Account(String accountId) {
    this.accountId = accountId;
    this.balance = BigDecimal.ZERO;
  }

  @JsonCreator
  public Account(@JsonProperty("accountId") String accountId,
    @JsonProperty("balance") BigDecimal balance) {
    this.accountId = accountId;
    this.balance = balance;
  }

  public String getAccountId() {
    return this.accountId;
  }

  public BigDecimal getBalance() {
    return this.balance;
  }

  public boolean equals(Object o) {
    if (o == this) return true;
    if (!(o instanceof Account)) return false;
    final Account other = (Account) o;
    final Object this$accountId = this.getAccountId();
    final Object other$accountId = other.getAccountId();
    if (this$accountId == null ? other$accountId != null : !this$accountId.equals(other$accountId)) return false;
    final Object this$balance = this.getBalance();
    final Object other$balance = other.getBalance();
    if (this$balance == null ? other$balance != null : !this$balance.equals(other$balance)) return false;
    return true;
  }

  public int hashCode() {
    final int PRIME = 59;
    int result = 1;
    final Object $accountId = this.getAccountId();
    result = result * PRIME + ($accountId == null ? 43 : $accountId.hashCode());
    final Object $balance = this.getBalance();
    result = result * PRIME + ($balance == null ? 43 : $balance.hashCode());
    return result;
  }

  public String toString() {
    return "Account(accountId=" + this.getAccountId() + ", balance=" + this.getBalance() + ")";
  }

  public void setBalance(BigDecimal balance) {
    this.balance = balance;
  }
}
