package ui.gdx;

import com.badlogic.gdx.ApplicationAdapter;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.Color;
import com.badlogic.gdx.graphics.GL20;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.GlyphLayout;
import com.badlogic.gdx.graphics.g2d.SpriteBatch;
import com.badlogic.gdx.graphics.glutils.ShapeRenderer;
import com.badlogic.gdx.math.Rectangle;
import com.badlogic.gdx.utils.Align;
import model.entities.GameOption;
import model.entities.Player;
import rules.variable.IRule;
import ui.visual.VisualGameSession;

import java.util.ArrayList;
import java.util.List;


/**
 * LibGDX desktop surface for the main Zilch play path.
 */
public class ZilchGdxGame extends ApplicationAdapter
{
	private static final float MIN_WIDTH = 920f;
	private static final float MIN_HEIGHT = 620f;
	private static final Color BACKGROUND = new Color(0.07f, 0.08f, 0.09f, 1f);
	private static final Color PANEL = new Color(0.13f, 0.15f, 0.17f, 1f);
	private static final Color PANEL_ALT = new Color(0.18f, 0.19f, 0.20f, 1f);
	private static final Color PRIMARY = new Color(0.13f, 0.48f, 0.58f, 1f);
	private static final Color PRIMARY_DARK = new Color(0.08f, 0.34f, 0.42f, 1f);
	private static final Color WARNING = new Color(0.74f, 0.45f, 0.15f, 1f);
	private static final Color SUCCESS = new Color(0.18f, 0.50f, 0.28f, 1f);
	private static final Color DISABLED = new Color(0.25f, 0.27f, 0.29f, 1f);
	private static final Color TEXT = new Color(0.93f, 0.94f, 0.91f, 1f);
	private static final Color MUTED_TEXT = new Color(0.68f, 0.71f, 0.70f, 1f);
	private static final Color DIE_FACE = new Color(0.91f, 0.88f, 0.78f, 1f);
	private static final Color DIE_PIP = new Color(0.08f, 0.08f, 0.08f, 1f);

	private final VisualGameSession session = new VisualGameSession();
	private final List<UiButton> buttons = new ArrayList<>();
	private SpriteBatch batch;
	private ShapeRenderer shapes;
	private BitmapFont font;
	private GlyphLayout glyphLayout;
	private float width;
	private float height;

	@Override
	public void create() {
		batch = new SpriteBatch();
		shapes = new ShapeRenderer();
		font = new BitmapFont();
		font.setUseIntegerPositions(false);
		glyphLayout = new GlyphLayout();
	}

	@Override
	public void render() {
		width = Math.max(MIN_WIDTH, Gdx.graphics.getWidth());
		height = Math.max(MIN_HEIGHT, Gdx.graphics.getHeight());
		buttons.clear();

		Gdx.gl.glClearColor(BACKGROUND.r, BACKGROUND.g, BACKGROUND.b, BACKGROUND.a);
		Gdx.gl.glClear(GL20.GL_COLOR_BUFFER_BIT);

		drawBackground();
		if (session.getPhase() == VisualGameSession.Phase.SETUP) {
			drawSetup();
		} else {
			drawGame();
		}
		handleTouch();
	}

	@Override
	public void dispose() {
		batch.dispose();
		shapes.dispose();
		font.dispose();
	}

	private void drawBackground() {
		fillRect(0, 0, width, height, BACKGROUND);
		fillRect(0, height - 78, width, 78, new Color(0.10f, 0.12f, 0.13f, 1f));
		drawText("Zilch Basic", 32, height - 28, 1.55f, TEXT);
		drawText("LibGDX visual play surface", 34, height - 56, 0.92f, MUTED_TEXT);
	}

	private void drawSetup() {
		float margin = 32f;
		float top = height - 108f;
		float panelHeight = height - 148f;
		float leftWidth = width * 0.56f;
		float rightX = margin + leftWidth + 22f;
		float rightWidth = width - rightX - margin;

		fillRect(margin, 28, leftWidth, panelHeight, PANEL);
		fillRect(rightX, 28, rightWidth, panelHeight, PANEL_ALT);

		drawText("Setup Rules", margin + 24, top, 1.2f, TEXT);
		drawWrapped(
				"Canonical defaults enable core scoring, First-Roll Bust, Final Chase, and ties. Stealing is optional and starts off.",
				margin + 24,
				top - 34,
				leftWidth - 48,
				0.86f,
				MUTED_TEXT
		);

		List<IRule> selectableRules = session.getSelectableRules();
		float y = top - 118f;
		float ruleStep = selectableRules.size() <= 1
				? 58f
				: Math.min(58f, (y - 44f) / (selectableRules.size() - 1));
		float ruleButtonHeight = ruleStep < 54f ? 34f : 38f;
		float ruleDescriptionOffset = ruleStep < 54f ? 8f : 10f;
		float ruleDescriptionScale = ruleStep < 54f ? 0.70f : 0.74f;
		for (IRule rule : selectableRules) {
			boolean enabled = session.isRuleEnabled(rule);
			Color buttonColor = enabled ? SUCCESS : DISABLED;
			addButton(
					(enabled ? "On  " : "Off ") + rule.getDisplayName(),
					margin + 24,
					y,
					leftWidth - 48,
					ruleButtonHeight,
					buttonColor,
					true,
					() -> session.toggleRule(rule)
			);
			drawText(
					rule.getDescription(),
					margin + 42,
					y - ruleDescriptionOffset,
					ruleDescriptionScale,
					MUTED_TEXT
			);
			y -= ruleStep;
		}

		drawText("Players", rightX + 24, top, 1.08f, TEXT);
		drawText(String.valueOf(session.getPlayerCount()), rightX + 142, top, 1.08f, TEXT);
		addButton("-", rightX + 24, top - 60, 54, 42, PRIMARY_DARK, session.getPlayerCount() > VisualGameSession.MIN_PLAYERS, () -> session.adjustPlayerCount(-1));
		addButton("+", rightX + 88, top - 60, 54, 42, PRIMARY, session.getPlayerCount() < VisualGameSession.MAX_PLAYERS, () -> session.adjustPlayerCount(1));

		drawText("Winning Score", rightX + 24, top - 132, 1.08f, TEXT);
		drawText(String.valueOf(session.getScoreLimit()), rightX + 160, top - 132, 1.08f, TEXT);
		addButton("-500", rightX + 24, top - 192, 82, 42, PRIMARY_DARK, session.getScoreLimit() > VisualGameSession.MIN_SCORE_LIMIT, () -> session.adjustScoreLimit(-1));
		addButton("+500", rightX + 116, top - 192, 82, 42, PRIMARY, true, () -> session.adjustScoreLimit(1));

		drawText("Opening Score", rightX + 24, top - 252, 1.08f, TEXT);
		drawText(String.valueOf(session.getOpeningScoreLimit()), rightX + 160, top - 252, 1.08f, TEXT);
		addButton("-250", rightX + 24, top - 312, 82, 42, PRIMARY_DARK, session.getOpeningScoreLimit() > VisualGameSession.MIN_OPENING_SCORE_LIMIT, () -> session.adjustOpeningScoreLimit(-1));
		addButton("+250", rightX + 116, top - 312, 82, 42, PRIMARY, session.getOpeningScoreLimit() < session.getScoreLimit(), () -> session.adjustOpeningScoreLimit(1));

		addButton("Start Visual Game", rightX + 24, 88, rightWidth - 48, 52, session.canStart() ? PRIMARY : DISABLED, session.canStart(), session::startGame);
		drawWrapped(session.getNotice(), rightX + 24, 64, rightWidth - 48, 0.84f, MUTED_TEXT);
	}

	private void drawGame() {
		float margin = 28f;
		float top = height - 104f;
		float scoreboardWidth = 260f;
		float actionWidth = 320f;
		float centerX = margin + scoreboardWidth + 20f;
		float centerWidth = width - centerX - actionWidth - margin - 20f;
		Player currentPlayer = session.getCurrentPlayer();

		fillRect(margin, 28, scoreboardWidth, height - 132, PANEL);
		fillRect(centerX, 28, centerWidth, height - 132, PANEL_ALT);
		fillRect(width - margin - actionWidth, 28, actionWidth, height - 132, PANEL);

		drawText("Scoreboard", margin + 20, top, 1.08f, TEXT);
		drawScoreboard(margin + 20, top - 38, scoreboardWidth - 40);

		String playerName = currentPlayer == null ? "" : currentPlayer.name();
		drawText(playerName + "'s Turn", centerX + 24, top, 1.22f, TEXT);
		drawText("Dice in play: " + session.getDiceInPlay(), centerX + 24, top - 32, 0.9f, MUTED_TEXT);
		drawDice(centerX + 24, top - 170, centerWidth - 48);
		drawWrapped(session.getNotice(), centerX + 24, 88, centerWidth - 48, 0.88f, TEXT);

		drawText("Actions", width - margin - actionWidth + 22, top, 1.08f, TEXT);
		drawActions(width - margin - actionWidth + 22, top - 54, actionWidth - 44);
	}

	private void drawScoreboard(float x, float y, float panelWidth) {
		for (Player player : session.getPlayers()) {
			boolean active = player == session.getCurrentPlayer();
			Color rowColor = active ? new Color(0.21f, 0.29f, 0.30f, 1f) : new Color(0.16f, 0.17f, 0.18f, 1f);
			fillRect(x, y - 36, panelWidth, 48, rowColor);
			drawText(player.name(), x + 12, y - 3, 0.88f, active ? TEXT : MUTED_TEXT);
			drawText("Banked " + player.score().getPermanentScore(), x + 12, y - 23, 0.72f, MUTED_TEXT);
			drawText("Round " + player.score().getRoundScore(), x + panelWidth - 96, y - 23, 0.72f, MUTED_TEXT);
			y -= 58;
		}

		if (session.isFinalRound() && session.getGameEndingPlayer() != null) {
			drawWrapped(
					"Final Chase triggered by " + session.getGameEndingPlayer().name() + ".",
					x,
					88,
					panelWidth,
					0.78f,
					WARNING
			);
		}
	}

	private void drawDice(float x, float y, float areaWidth) {
		List<Integer> diceValues = session.getCurrentDiceValues();
		int diceCount = diceValues.isEmpty() ? Math.max(1, session.getDiceInPlay()) : diceValues.size();
		float dieSize = Math.min(82, (areaWidth - ((diceCount - 1) * 14)) / Math.max(1, diceCount));
		float totalWidth = (diceCount * dieSize) + ((diceCount - 1) * 14);
		float startX = x + Math.max(0, (areaWidth - totalWidth) / 2f);

		for (int index = 0; index < diceCount; index++) {
			int value = diceValues.isEmpty() ? 0 : diceValues.get(index);
			drawDie(startX + index * (dieSize + 14), y, dieSize, value);
		}
	}

	private void drawActions(float x, float y, float panelWidth) {
		VisualGameSession.Phase phase = session.getPhase();
		if (phase == VisualGameSession.Phase.AWAITING_STEAL_DECISION) {
			addButton("Continue / Steal", x, y, panelWidth, 48, SUCCESS, true, session::steal);
			addButton("Fresh Roll", x, y - 62, panelWidth, 48, PRIMARY, true, session::freshRoll);
			return;
		}

		if (phase == VisualGameSession.Phase.AWAITING_ROLL) {
			addButton("Roll Dice", x, y, panelWidth, 48, PRIMARY, true, session::roll);
			addButton("Back To Setup", x, 70, panelWidth, 42, DISABLED, true, session::resetToSetup);
			return;
		}

		if (phase == VisualGameSession.Phase.AWAITING_OPTION) {
			drawText("Scoring Options", x, y + 28, 0.92f, MUTED_TEXT);
			float optionY = y - 24;
			for (GameOption option : session.getCurrentOptions()) {
				String value = option.selectedValue() == null ? "" : " [" + option.selectedValue() + "]";
				String label = option.displayName() + value + " - " + option.pointsAwarded();
				addButton(label, x, optionY, panelWidth, 42, PRIMARY, true, () -> session.chooseOption(option));
				optionY -= 50;
			}
			return;
		}

		if (phase == VisualGameSession.Phase.AWAITING_DECISION) {
			addButton("Roll Again", x, y, panelWidth, 48, PRIMARY, true, session::rollAgain);
			float bankY = y - 62;
			if (session.canScoreMore()) {
				addButton("Score More", x, bankY, panelWidth, 48, WARNING, true, session::scoreMore);
				bankY -= 62;
			}
			addButton("Bank Round", x, bankY, panelWidth, 48, session.canBankCurrentTurn() ? SUCCESS : DISABLED, session.canBankCurrentTurn(), session::bank);
			if (!session.canBankCurrentTurn()) {
				drawWrapped(
						"Banking unlocks after the round reaches " + session.getOpeningScoreLimit() + " points.",
						x,
						bankY - 74,
						panelWidth,
						0.78f,
						MUTED_TEXT
				);
			}
			return;
		}

		if (phase == VisualGameSession.Phase.GAME_OVER) {
			addButton("New Game", x, y, panelWidth, 48, PRIMARY, true, session::resetToSetup);
		}
	}

	private void drawDie(float x, float y, float size, int value) {
		fillRect(x, y, size, size, DIE_FACE);
		strokeRect(x, y, size, size, new Color(0.55f, 0.52f, 0.45f, 1f));
		if (value == 0) {
			drawText("?", x + size * 0.41f, y + size * 0.62f, 1.2f, DIE_PIP);
			return;
		}

		float left = x + size * 0.27f;
		float mid = x + size * 0.50f;
		float right = x + size * 0.73f;
		float bottom = y + size * 0.28f;
		float center = y + size * 0.50f;
		float top = y + size * 0.72f;
		float radius = Math.max(4, size * 0.055f);

		if (value == 1 || value == 3 || value == 5) {
			fillCircle(mid, center, radius, DIE_PIP);
		}
		if (value >= 2) {
			fillCircle(left, top, radius, DIE_PIP);
			fillCircle(right, bottom, radius, DIE_PIP);
		}
		if (value >= 4) {
			fillCircle(right, top, radius, DIE_PIP);
			fillCircle(left, bottom, radius, DIE_PIP);
		}
		if (value == 6) {
			fillCircle(left, center, radius, DIE_PIP);
			fillCircle(right, center, radius, DIE_PIP);
		}
	}

	private void addButton(String label, float x, float y, float w, float h, Color color, boolean enabled, Runnable action) {
		Color fill = enabled ? color : DISABLED;
		fillRect(x, y, w, h, fill);
		strokeRect(x, y, w, h, enabled ? new Color(0.58f, 0.67f, 0.67f, 1f) : new Color(0.36f, 0.37f, 0.38f, 1f));
		drawCenteredText(label, x, y, w, h, 0.86f, enabled ? TEXT : MUTED_TEXT);
		buttons.add(new UiButton(new Rectangle(x, y, w, h), enabled, action));
	}

	private void handleTouch() {
		if (!Gdx.input.justTouched()) {
			return;
		}

		float touchX = Gdx.input.getX();
		float touchY = height - Gdx.input.getY();
		for (UiButton button : buttons) {
			if (button.enabled && button.bounds.contains(touchX, touchY)) {
				button.action.run();
				return;
			}
		}
	}

	private void fillRect(float x, float y, float w, float h, Color color) {
		shapes.begin(ShapeRenderer.ShapeType.Filled);
		shapes.setColor(color);
		shapes.rect(x, y, w, h);
		shapes.end();
	}

	private void strokeRect(float x, float y, float w, float h, Color color) {
		shapes.begin(ShapeRenderer.ShapeType.Line);
		shapes.setColor(color);
		shapes.rect(x, y, w, h);
		shapes.end();
	}

	private void fillCircle(float x, float y, float radius, Color color) {
		shapes.begin(ShapeRenderer.ShapeType.Filled);
		shapes.setColor(color);
		shapes.circle(x, y, radius, 24);
		shapes.end();
	}

	private void drawText(String text, float x, float y, float scale, Color color) {
		batch.begin();
		font.setColor(color);
		font.getData().setScale(scale);
		font.draw(batch, text, x, y);
		batch.end();
	}

	private void drawWrapped(String text, float x, float y, float wrapWidth, float scale, Color color) {
		batch.begin();
		font.setColor(color);
		font.getData().setScale(scale);
		font.draw(batch, text, x, y, wrapWidth, Align.left, true);
		batch.end();
	}

	private void drawCenteredText(String text, float x, float y, float w, float h, float scale, Color color) {
		batch.begin();
		font.setColor(color);
		font.getData().setScale(scale);
		glyphLayout.setText(font, text);
		font.draw(batch, text, x + (w - glyphLayout.width) / 2f, y + (h + glyphLayout.height) / 2f);
		batch.end();
	}

	private record UiButton(Rectangle bounds, boolean enabled, Runnable action) {}
}
