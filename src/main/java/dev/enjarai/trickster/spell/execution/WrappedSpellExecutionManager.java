package dev.enjarai.trickster.spell.execution;

import java.util.List;
import java.util.Optional;

import dev.enjarai.trickster.Trickster;
import dev.enjarai.trickster.advancement.criterion.ModCriteria;
import dev.enjarai.trickster.spell.Fragment;
import dev.enjarai.trickster.spell.SpellExecutor;
import dev.enjarai.trickster.spell.SpellPart;
import dev.enjarai.trickster.spell.blunder.BlunderException;
import dev.enjarai.trickster.spell.blunder.NaNBlunder;
import dev.enjarai.trickster.spell.execution.executor.DefaultSpellExecutor;
import dev.enjarai.trickster.spell.execution.source.SpellSource;
import net.minecraft.text.Text;

public class WrappedSpellExecutionManager implements SpellExecutionManager {
    private SpellExecutionManager inner;
    private Optional<DefaultSpellExecutor> executor = Optional.empty();

    public WrappedSpellExecutionManager(SpellExecutionManager inner) {
        this.inner = inner;
    }

    public void tick(SpellSource source,
        PlayerSpellExecutionManager.ExecutorCallback tickCallback,
        PlayerSpellExecutionManager.ExecutorSpellCallback completeCallback,
        PlayerSpellExecutionManager.ExecutorCallback errorCallback) {
        executor.ifPresent(executor -> {
            try {
                var result = executor.run(source);
                if (result.isEmpty()) {
                    tickCallback.callTheBack(0, executor);
                } else {
                    completeCallback.callTheBack(0, executor, result.get());
                    this.executor = Optional.empty();
                }
            } catch (BlunderException blunder) {
                var message = blunder.createMessage()
                    .append(" (").append(executor.getDeepestState().formatStackTrace()).append(")");

                if (blunder instanceof NaNBlunder)
                    source.getPlayer().ifPresent(ModCriteria.NAN_NUMBER::trigger);

                source.getPlayer().ifPresent(player -> player.sendMessage(message));
                errorCallback.callTheBack(0, executor);
                this.executor = Optional.empty();
            } catch (Throwable e) {
                var message = Text.literal("Uncaught exception in spell: " + e.getMessage())
                    .append(" (").append(executor.getDeepestState().formatStackTrace()).append(")");

                Trickster.LOGGER.error("Uncaught error in spell:", e);

                source.getPlayer().ifPresent(player -> player.sendMessage(message));
                errorCallback.callTheBack(0, executor);
                this.executor = Optional.empty();
            }
        });
    }

    public boolean setSpell(SpellPart spell, List<Fragment> arguments) {
        this.executor = Optional.of(new DefaultSpellExecutor(spell, new ExecutionState(arguments)));
        return true;
    }

    public boolean setSpell(SpellPart spell) {
        return setSpell(spell, List.of());
    }

    public Optional<DefaultSpellExecutor> getExecutor() {
        return executor;
    }

    public void setExecutor(Optional<DefaultSpellExecutor> executor) {
        this.executor = executor;
    }

    public void killOuter() {
        executor = Optional.empty();
    }

    public void setInner(SpellExecutionManager inner) {
        this.inner = inner;
    }

    @Override
    public Optional<Integer> queue(SpellExecutor executor) {
        return inner.queue(executor);
    }

    @Override
    public boolean kill(int index) {
        return inner.kill(index);
    }

    @Override
    public Optional<SpellExecutor> getSpellExecutor(int index) {
        return inner.getSpellExecutor(index);
    }

    @Override
    public Optional<SpellPart> getSpell(int index) {
        return inner.getSpell(index);
    }

    @Override
    public void killAll() {
        executor = Optional.empty();
        inner.killAll();
    }
}
