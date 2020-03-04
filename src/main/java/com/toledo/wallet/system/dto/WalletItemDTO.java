package com.toledo.wallet.system.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

import org.hibernate.validator.constraints.Length;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.toledo.wallet.business.domain.WalletItem;
import com.toledo.wallet.business.domain.enums.WalletItemType;
import com.toledo.wallet.system.util.DateUtil;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class WalletItemDTO implements Serializable {
	private static final long serialVersionUID = 5724078994825678518L;

	private Long id;
	
	@NotNull(message = "A data foi omitida.")
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	private Date date;
	
	@NotNull(message = "O tipo do item foi omitido.")
	@Pattern(regexp = "^(INPUT|OUTPUT)$", message = "O tipo do item deve ser definido como INPUT ou OUTPUT")
	private String type;
	
	@NotNull(message = "A descrição do item foi omitida.")
	@Length(min = 5, message = "A descrição deve ter 5 ou mais caracteres.")
	private String description;
	
	@NotNull(message = "O valor do item foi omitido.")
	private BigDecimal value;
	
	public WalletItemDTO(WalletItem entity){
		this.id = entity.getId();
		this.date = entity.getDate();
		this.type = entity.getType().getValue();
		this.description = entity.getDescription();
		this.value = entity.getValue();
	}
	
	public WalletItem toEntity() {
		return new WalletItem(id, null, date, WalletItemType.toEnum(type), description, value);
	}
}
