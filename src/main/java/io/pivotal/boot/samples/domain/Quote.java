package io.pivotal.boot.samples.domain;


import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

/**
 * @author Vinicius Carvalho
 */
public class Quote {

	@JsonProperty("Symbol")
	private String symbol;
	@JsonProperty("Name")
	private String name;
	@JsonProperty("Ask")
	private Double ask;
	@JsonProperty("Change")
	private Double change;
	@JsonProperty("DaysLow")
	private Double daysLow;
	@JsonProperty("DaysHigh")
	private Double daysHigh;
	@JsonProperty("Open")
	private Double open;
	@JsonProperty("PreviousClose")
	private Double previousClose;

	public Double getOpen() {
		return open;
	}

	public void setOpen(Double open) {
		this.open = open;
	}

	public Double getPreviousClose() {
		return previousClose;
	}

	public void setPreviousClose(Double previousClose) {
		this.previousClose = previousClose;
	}

	public String getSymbol() {
		return symbol;
	}

	public void setSymbol(String symbol) {
		this.symbol = symbol;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Double getAsk() {
		return ask;
	}

	public void setAsk(Double ask) {
		this.ask = ask;
	}

	public Double getChange() {
		return change;
	}

	public void setChange(Double change) {
		this.change = change;
	}

	public Double getDaysLow() {
		return daysLow;
	}

	public void setDaysLow(Double daysLow) {
		this.daysLow = daysLow;
	}

	public Double getDaysHigh() {
		return daysHigh;
	}

	public void setDaysHigh(Double daysHigh) {
		this.daysHigh = daysHigh;
	}

	@JsonIgnore
	public Double getDifference(){
		return ask - previousClose;
	}

	public Quote(){}

	public Quote(String csv){
		String[] contents = csv.split(",");
		this.symbol = contents[0];
		this.name = contents[1];
		this.ask = parseDouble(contents[2]);
		this.change = parseDouble(contents[3]);
		this.daysHigh = parseDouble(contents[4]);
		this.daysLow = parseDouble(contents[5]);
		this.open = parseDouble(contents[6]);
		this.previousClose = parseDouble(contents[7]);
	}

	@Override
	public String toString() {
		final StringBuffer sb = new StringBuffer("Quote{");
		sb.append("symbol='").append(symbol).append('\'');
		sb.append(", name='").append(name).append('\'');
		sb.append(", ask=").append(ask);
		sb.append(", change=").append(change);
		sb.append(", daysLow=").append(daysLow);
		sb.append(", daysHigh=").append(daysHigh);
		sb.append('}');
		return sb.toString();
	}

	private Double parseDouble(String value){
		double number = 0.0;

		if(value.isEmpty() || value == null)
			return 0.0;
		try{
			number = Double.parseDouble(value);
		}catch (Exception e){}

		return number;
	}
}
