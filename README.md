# Zilch Basic

LibGDX desktop Zilch implementation built around:

- dynamic rule discovery
- an explicit turn state machine
- a simple in-process event system
- creator/builder/factory wiring for bootstrapping a game
- a visual, event-loop friendly play surface backed by the same core rules

The current `main` path launches the LibGDX visual game. The previous console
menu path is preserved as `client.ZilchCliClient` and on the `cli-menu` branch.

## Running

```bash
./gradlew run
```

To run the preserved console flow from this branch:

```bash
./gradlew run --args='cli'
```

To run tests:

```bash
./gradlew test
```

## Architecture Map

The diagram below shows how the main classes connect at runtime.

```mermaid
flowchart LR
    ZC["ZilchClient"]
    ZCLI["ZilchCliClient"]

    subgraph UI["UI / Setup"]
        GDX["ZilchGdxGame"]
        VGS["VisualGameSession"]
        UIM["UserInteractionManager"]
        CM["ConsoleMessage"]
        CIM["ConsoleInputManager"]
    end

    subgraph Creation["Game Construction"]
        GC["GameCreator"]
        AGSC["AbstractGameServerCreator"]
        GF["GameFactory"]
        GB["GameBuilder"]
        GID["GameIDManager"]
    end

    subgraph Server["Game Runtime"]
        GS["GameServer"]
        GE["GameEngine"]
        GSM["GameStateManager"]
        AGSM["AbstractGameStateManager"]
        TC["TurnContext"]
        ST["StartTurnState"]
        RD["RollDiceState"]
        EO["EvaluateOptionsState"]
        SO["SelectOptionState"]
        AO["ApplyOptionState"]
        DT["DecideTurnState"]
        ET["EndTurnState"]
    end

    subgraph Rules["Rules"]
        GOM["GameOptionManager"]
        RM["RuleManager"]
        RR["RuleRegistry"]
        RS["RuleScanner"]
        IR["IRule"]
        SR["SingleRule"]
        MR["MultipleRule"]
        AMR["AddMultipleRule"]
        SER["SetRule"]
        STR["StraitRule"]
        FBR["FirstRollBustRule"]
    end

    subgraph Model["Game Data / Managers"]
        ACT["ActionManager"]
        PM["PlayerManager"]
        DM["DiceManager"]
        P["Player"]
        D["Dice"]
        S["Score"]
        GO["GameOption"]
        RC["RuleContext"]
    end

    subgraph Events["Events"]
        DISP["SimpleEventDispatcher"]
        EVT["Event"]
        GOL["GameOverListener"]
    end

    ZC --> GDX
    ZCLI --> UIM
    ZCLI --> GC
    ZCLI --> GID

    GDX --> VGS
    VGS --> ACT
    VGS --> GOM
    VGS --> PM
    VGS --> DM

    UIM --> CM
    UIM --> CIM

    GC --> GF
    GC --> GB
    GF --> AGSC
    GB --> AGSC
    AGSC --> DISP
    AGSC --> ACT
    AGSC --> RM
    AGSC --> GS

    GS --> GE
    GS --> ACT
    GS --> DISP
    GS --> GOM
    GS --> GOL

    GE --> GSM
    GSM --> AGSM
    GSM --> ST
    GSM --> RD
    GSM --> EO
    GSM --> SO
    GSM --> AO
    GSM --> DT
    GSM --> ET
    ST --> TC
    RD --> TC
    EO --> TC
    SO --> TC
    AO --> TC
    DT --> TC
    ET --> TC
    TC --> RC

    EO --> GOM
    SO --> GOM
    AO --> GOM
    GOM --> RM
    RM --> RR
    RR --> RS
    RR --> IR
    IR --> SR
    IR --> MR
    IR --> AMR
    IR --> SER
    IR --> STR
    IR --> FBR
    RM --> GO

    ST --> ACT
    RD --> ACT
    AO --> ACT
    DT --> ACT
    ACT --> PM
    ACT --> DM
    PM --> P
    P --> D
    P --> S
    RC --> P

    GS --> EVT
    DISP --> GOL
    GOL --> ACT
    GOL --> GS
    GOL --> UIM
```

## Turn State Machine

This is the per-turn flow driven by `GameStateManager`.

```mermaid
stateDiagram-v2
    [*] --> START_TURN
    START_TURN --> ROLL_DICE
    ROLL_DICE --> EVALUATE_OPTIONS
    EVALUATE_OPTIONS --> ROLL_DICE: first-roll bust / +50 points
    EVALUATE_OPTIONS --> END_TURN: later bust
    EVALUATE_OPTIONS --> SELECT_OPTION: options found
    SELECT_OPTION --> APPLY_OPTION
    APPLY_OPTION --> DECIDE_TURN
    DECIDE_TURN --> ROLL_DICE: roll again
    DECIDE_TURN --> END_TURN: bank round
    END_TURN --> [*]
```

## Key Notes

- `ZilchClient` now starts the LibGDX desktop interface. `ZilchCliClient` keeps the older interactive console menu available.
- `VisualGameSession` adapts the existing game rules to button-driven UI actions without blocking the LibGDX render loop.
- `RuleScanner` discovers concrete rule classes under `rules.variable`, so a new rule that implements the expected template can be loaded automatically.
- `RuleRegistry` separates discovered rules from active rules, and `UserInteractionManager` uses that discovered list to build setup options.
- Some setup options are game variants rather than scoring options, such as `First-Roll Bust`, which can award 50 points and reroll on a no-score opening roll.
- `TurnContext` is the mutable turn-local object passed across the entire state machine.
- `GameServer` owns the outer game loop, while `GameEngine` owns a single turn.
- `GameOverListener` bridges the event system back into gameplay by triggering the final-round flow.

## TODO

- Add more specialized listeners if more game events become meaningful.
- Continue cleanup and documentation polishing.
