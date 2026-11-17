# MSL Rubric Assessment Q1-Q2 2026: Vault Loader load order

Target: ITF sandbox. Nothing here has been loaded. Built from the "Duvyzat Brand Advocacy Survey" (`VC1ZZ25E891JMZH`) and the MSL rubric spreadsheet.

## Load order
1. `01_survey__v.csv` -> object `survey__v`, **Create**. One row.
2. `02_survey_question__v.csv` -> object `survey_question__v`, **Create**. 12 rows. Map `survey__v.external_id__v` to the survey lookup (match on `external_id__v`).
3. Do NOT load survey targets or responses (paused).

Object type: `object_type__v` is a column in both files (`recurring__v` on the survey; `description__v`, `picklist__v`, `long_text__v`, `text__v` on questions). If Loader asks for a single object type, load the questions in one pass per type.

## External IDs
- Survey: `MSL-RUBRIC-Q1Q2-2026`
- Questions: `MSL-RUBRIC-Q1Q2-2026-Q00` to `-Q11` (the suffix equals `order__v`)

## Required fields
- survey__v: `name__v`, `start_date__v`, `end_date__v` (status, lifecycle and state default).
- survey_question__v: `survey__v`, `order__v` (must start at 0 and increment by 1), `text__v`, `status__v`.

## Conventions used (verified in the sandbox / docs)
- `answer_choice__v` is `Choice1;Weight1;Choice2;Weight2`, semicolon-delimited, weight required for every choice (0 = unweighted, as the template does). Picklists max 20 choices. Choices cannot contain `;`.
- `territories__v` is `;name;name;` (leading and trailing semicolon).
- `account_types__v` and `channels__v` are semicolon lists of object-type names.
- Sections are modeled with `description__v` questions (no response collected). The template has none, so this is new to this Vault.

## Survey as built
| Field | Value | Source |
|---|---|---|
| Name | MSL Rubric Assessment Q1-Q2 2026 | you |
| Object type | recurring__v | template |
| Start / end | 2026-11-17 / 2028-04-30 | start = today; end copied from template |
| Status | published__v (survey_status__v) | you |
| Channels | crm__v | template |
| Target type / assignment | accounts__v / territory__v | template |
| Open / auto-target | true / true | template |
| Language | en_us__v | template |
| Product | blank | template (blank) |
| Account types | same 21 types as the template | template |
| Territories | M20101-M20107 (children of MSL) | **changed**: template uses TLL territories T20101-T20103 |
| share_team__v / itf_share_team__c | MSL / msl__c | **changed**: template uses TLL / tll__c |
| Owner | Jay Doherty (user id 35310407) | placeholder |

## Caveats before loading
- **Published via Loader may skip the publish process.** Loading `survey_status__v = published__v` does not run the Publish action, so auto-targeting and target ownership assignment may not happen. Because the survey is open, MSLs can create targets themselves from the call report, which does not need the publish step. If you want the standard publish behavior, load with `development__v` and use Publish in the UI.
- **Owner**: `ownerid__v` is Jay Doherty's user ID (placeholder). Reassign if needed.
- **question_number__v**: numbered 1-9 on real questions, blank on section headers. Check the display after load.
- Belief 3 title in the spreadsheet reads "Critcal"; corrected to "Critical" here.
- Sharing: `survey_msl__c` shares surveys with `itf_share_team__c = msl__c` to group `msls__c`.

## Verify after load
1. VQL: `SELECT id, name__v, survey_status__v, state__v, territories__v FROM survey__v WHERE external_id__v = 'MSL-RUBRIC-Q1Q2-2026'` returns one row.
2. VQL: `SELECT order__v, object_type__v, text__v FROM survey_question__v WHERE survey__v = '<id>' ORDER BY order__v` returns 12 rows, orders 0-11.
3. In the UI, open the survey: three section headers, three score pickers with 5 choices each, then the six shared questions.
4. As an MSL (profile `itf_msl_crm__c`), open an MSL Meeting Brief for an HCP and confirm the survey is offered in the Survey Targets section (Create).
5. Then deploy the report MDL (`Report/msl_rubric_scores_over_time__c`). It filters on the survey name, so the survey must already exist or the deploy fails.
