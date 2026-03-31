package de.dafuqs.chalk.config;

import me.shedaniel.autoconfig.ConfigData;
import me.shedaniel.autoconfig.annotation.Config;

@Config(name = "Chalk")
public class ChalkConfig implements ConfigData {
	
	public boolean EmitParticles = true;
	
	@Override
	public void validatePostLoad() {}
}
