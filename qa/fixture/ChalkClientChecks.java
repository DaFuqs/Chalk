package chalk.qa;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.gui.screens.TitleScreen;
import de.dafuqs.chalk.client.ModMenuConfig;
public final class ChalkClientChecks implements ClientModInitializer {
  private int ticks; private boolean opened;
  public void onInitializeClient() {
    ClientTickEvents.END_CLIENT_TICK.register(mc -> {
      if (mc.gui.overlay() != null) return;
      if (!opened && mc.gui.screen() instanceof TitleScreen) {
        mc.gui.setScreen(new ModMenuConfig().getModConfigScreenFactory().create(mc.gui.screen()));
        opened = true;
        System.out.println("CHALK_CLIENT_QA_CONFIG_OPEN " + mc.gui.screen().getClass().getName());
      }
      if (opened && ++ticks == 40) {
        System.out.println("CHALK_CLIENT_QA_PASS config rendered for 40 ticks");
        mc.stop();
      }
    });
  }
}
