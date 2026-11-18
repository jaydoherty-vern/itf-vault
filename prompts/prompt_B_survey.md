# Prompt B: MSL Rubric Assessment survey, ITF Vault CRM

You are configuring Veeva Vault CRM for ITF Therapeutics. The workspace at `C:\Users\Jay\itf-vault-workspace` is an MDL config pull from the ITF sandbox (`components\<ComponentType>\<name>\<name>.mdl`).

Inputs (read them first). They live OUTSIDE the workspace in `C:\Users\Jay\Claude\itf_migration\context_for_claude\`. Treat them as read-only; do not copy them into the repo or commit them. Read them by absolute path (you may need to approve access to that directory):
- `C:\Users\Jay\Claude\itf_migration\context_for_claude\Claude-ITF Configuration Requirements - Sheet1.csv`: this prompt covers ONLY the Survey rows (create the survey, embed it in the MSL Meeting Brief, produce Loader CSVs). Do not touch the CLM, Save for Later or Discussion Topics rows (that is Prompt A).
- `C:\Users\Jay\Claude\itf_migration\context_for_claude\MSL_Rubric_Assessment_Q1-Q2 2026 Updated.xlsx - Assessment Tracker.csv`: the rubric. Rows 1-2 are the header (belief descriptions are in row 1).

## Hard rules
1. Edit workspace files only. You have READ-ONLY access to the ITF sandbox `https://sb-itftherapeutics-itf-therapeutics-vault-crm-sandbox.veevavault.com` via the user's existing session (skill `vault:login`; read-only VQL via skill `vault:query`). Never write to any Vault and never read any other Vault. Do NOT run Vault Loader. The user loads the CSVs and deploys themselves.
2. Uncommitted OneKey-related changes exist in the working tree. Do NOT modify, stage or commit them. Run `git status` first.
3. Create a new branch off `main` (for example `feature/msl-rubric-survey`). Commit only files you created or changed, staged by explicit path (never `git add -A`). Do not push.
4. Survey, question and response records are DATA, not MDL. They do not exist as components in the repo. The deliverables are Loader CSVs plus any MDL needed for the report.

## Step 1: read the template (read-only VQL)
Find the survey named "Duvyzat Brand Advocacy Survey" in the sandbox. It is a `recurring__v` survey. Pull its `survey__v` record (all fields, especially object type, product, channels, account types, territories, assignment type, open, auto-target, language, country, dates, status) and every `survey_question__v` record (object type, order, question numbers, answer_choice format, required, condition, section or description questions). Show the user a summary table of its structure before building anything. Use it for: layout, how sections are modeled, question type usage, and targeting. The rubric spreadsheet wins wherever the two differ.

Also verify and report: (a) the exact `answer_choice__v` delimiter and whether choices can carry numeric weights, (b) how `question_response__v` stores picklist answers, (c) whether a user record exists for "Jay Doherty" (placeholder owner).

## Step 2: survey design (settled by the user)
- Name: "MSL Rubric Assessment" (use a clear name such as "MSL Rubric Assessment Q1-Q2 2026"; confirm the exact name in the report). Object type `recurring__v`.
- Structure: ONE SECTION PER BELIEF (3 sections), with the belief title and description from header row 1 as the section header, for example "BELIEF 1: Dose Modifications Expected" plus its description.
  - Each belief has exactly ONE question: the score picklist with exactly these 5 choices: `1 - Not Aligned`, `2 - Limited Alignment`, `3 - Basic Alignment`, `4 - Operationally Aligned`, `5 - Aligned & Confident`.
  - Shared questions after the belief sections, taken from the rubric columns: Updated This Quarter? (picklist of the clean values used in the sheet: Yes and the "No - ..." variants), Basis for Assessment (picklist of the clean values used in the sheet), Rating Confidence (High / Medium / Low), Evidence / Notes (long text), Follow-up Needed? (Yes / No), Follow-up Owner (text).
  - Prior Score, Score Used and Change vs Prior are NOT questions.
  - Derive the picklist values from the actual CSV; ignore junk values ("Remove", "Baseline", "n/a baseline", stray characters). List any ambiguous values in the report.
- Targeting: copy from the Duvyzat template (product, channels, account types, territories, assignment type, open, auto-target, language, country), except: new name, status Published, start date = today (2026-11-17 unless the user says otherwise). If the template targeting looks unusable for the MSL profiles, say so.
- Question types: use the same object types as the template for the equivalent question kinds.

## Step 3: Loader CSVs
Write to `loader_csvs\` in the workspace:
- `01_survey__v.csv`, `02_survey_question__v.csv`, plus `README_load_order.md` (load order, object types, external-ID scheme, required fields, and how to verify after load). Use external IDs for survey and questions so questions can reference the survey. Match the template's field set and Loader conventions (check `Surveys > Creating > Data Loading Surveys` in the Vault CRM docs through the Vault MCP).
- Do NOT create a response (survey_target / question_response) CSV. The user paused that.
- Do NOT write a client-questions list.

## Step 4: embed on the MSL Meeting Brief
The `call2__v.mslmeetingbrief__v` layouts already include the standard `survey_target_section__v` section (for example `Pagelayout\itf_msl_meeting_brief__c\page_markup.xml`), so the new survey should appear through targeting, with no layout change. Confirm this against the layout files and the docs (survey target display criteria on the call report: account, channels include CRM, start and end dates, `ownerid__v`, product, status Pending or Saved). Change a layout only if needed and say so.

## Step 5: change-over-time report
Goal: track each HCP's score over time. Native support: recurring surveys keep one `survey_target__v` per submission, with `question_response__v` records, plus an Update Response button. There is no native "change vs prior" calculation. Do not build a trigger or a calculated field.
Use skill `vault-administrator:reporting` to build a Vault report (report type and report) as MDL in the workspace:
- Data: `question_response__v` joined to `survey_target__v` and the account, filtered to the new survey.
- Columns: HCP, NPI, MSL (survey target owner), question, answer, submitted date.
- Group by HCP, then question; sort by submitted date (so scores read as a series).
- No dashboard. Visibility: only profiles `itf_msl_crm__c` and `itf_msl_manager_crm__c`, plus admin. Use the repo's existing permission set or security profile pattern. Do not grant it to other profiles.
Note in the report how to get the numeric delta (compare rows, or use the survey results export which has a Most Recent column), and whether weighted answer choices could supply a numeric score.

## Report (final message)
1. Template structure summary and the facts verified in Step 1.
2. Files created or changed (explicit list), branch and commit hash.
3. Survey design as built (sections, questions, choices, targeting) and every assumption.
4. Load order and manual follow-ups for the user (load CSVs, deploy MDL, assign the report permission, test on a real MSL call).
5. Open questions.
6. Remind the user that OneKey changes remain uncommitted and need their review.
