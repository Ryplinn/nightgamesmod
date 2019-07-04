# nightgamesmod stable

My previous rewrite attempt in explicit-loops stalled, partly due to trying to keep up with features and systems added on other forks.

This stable branch is feature-frozen at v2.5.1.2 of nergantre's master branch. Commits to this branch will be either bugfixes or refactoring.

--Ryplinn

## Steps taken/planned

1. **DONE** Some tests are failing or broken. Restore to green status.

1. **DONE [GUI BREAKUP]** Move game logic out of GUI. For this step, code reaching into the gui package is fine; code reaching out is not. Might look uglier than before.

1. **DONE [GLOBAL BREAKUP]** Break up the Global monstrosity. Find similar parts and move them to their own classes/packages.

   **ENOUGH** This looks like a good time to do some post-move cleanup.

1. **DONE** Make the game loop explicit, or at least document it. (holy balls that was way easier than last time!
    amazing what focusing on one thing at a time will do)

1. **[STATIC DISCHARGE]** Reduce reliance on static fields and methods. Static members make testing harder and can
    introduce spooky action at a distance.

1. **IT'S BETTER NOW AT LEAST** Concurrency is hard! Build infrastructure to make it easy to do things like wait for user input.

1. **DONE** Make the night-time match loop explicit.

   **DONE** Kludge combat into compatibility with the match loop so I can playtest.

   **DONE** I want to work out why the main text window is so stuttery/flickery now. I'd like to figure out a way to only paint
   the text when the player is about to be presented with an interactable prompt.

1. **DONE** Make the combat loop explicit.

    **CODER BONUS** Providing an save game file as an argument to the main function will now launch that game directly. No more clicking through dialog boxes while debugging! (If we already had a way to do that, I'm gonna feel silly)

1. **DONE** Make the daytime loop explicit.

1. **PRETTY GOOD** Fix everything I broke in the previous steps.

1. Migrate combat to an event-based system, or at least some sort of system.
    1. Specify and unify damage system. Things are pretty ad-hoc right now.
    1. Add hooks to combat phases, movement phases, taking and receiving damage, daytime activities, etc. that trait and status specifications can use to apply their effects.
    1. Migrate existing traits and statuses to use this system.

1. Separate display and game logic. There will need to be a layer between display logic and game logic, although exactly where that code lives is less important than the conceptual structure.

    1. HTML styling should be handled on the display style. Having on-screen text stored in an easy-to-parse/compare format somewhere would be helpful.

1. Make most trait and status effects specified in data files, rather than code.

1. Create an editor?

1. man who even knows

1. anything that isn't done yet will probably change
