package com.toledo.wallet.business.domain;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.toledo.wallet.business.domain.enums.WalletItemType;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "wallet_items")
public class WalletItem implements Serializable {
	private static final long serialVersionUID = -1245409993121756747L;

	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "wallet", referencedColumnName = "id", nullable = false)
	private Wallet wallet;
	
	@Temporal(TemporalType.DATE)
	private Date date;
	
	@Enumerated(EnumType.STRING)
	private WalletItemType type;
	private String description;
	private BigDecimal value;
}
