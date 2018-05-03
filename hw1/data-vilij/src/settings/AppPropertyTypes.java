package settings;

/**
 * This enumerable type lists the various application-specific property types listed in the initial set of properties to
 * be loaded from the workspace properties <code>xml</code> file specified by the initialization parameters.
 *
 * @author Ritwik Banerjee
 * @see vilij.settings.InitializationParams
 */
public enum AppPropertyTypes {

    /* resource files and folders */
    DATA_RESOURCE_PATH,
    CSS_PATH,
    CSS_FILE_PATH,

    /* user interface icon file names */
    SCREENSHOT_ICON,

    /* tooltips for user interface buttons */
    SCREENSHOT_TOOLTIP,

    /* error messages */
    RESOURCE_SUBDIR_NOT_FOUND,
    LOAD_ERROR_TITLE,
    EXIT_WHILE_RUNNING_WARNING,
    EXIT_WITHOUT_SAVING,

    /* application-specific message titles */
    SAVE_UNSAVED_WORK_TITLE,

    /* application-specific messages */
    SAVE_UNSAVED_WORK,

    /* application-specific parameters */
    DATA_FILE_EXT,
    DATA_FILE_EXT_DESC,
    IMAGE_FILE_EXT,
    IMAGE_FILE_EXT_DESC,
    TEXT_AREA,
    SPECIFIED_FILE,
    OPEN_LABEL,
    READ_ONLY_ON,
    READ_ONLY_OFF,
    EXCEPTION_LABEL,
    AVERAGE_LABEL,

    /*CSS-specific parameters*/
    AVERAGE_LINE,
    CHART_BACKGROUND,
    CHART_PLOT_BACKGROUND,
    AXIS,
    AXIS_TICK_MARK,
    AXIS_MINOR_TICK_MARK,

    /*TEXTS*/
    TITLE_FOR_GREATER_THAN_TEN_LINES,
    MESSAGE_PT_1,
    MESSAGE_PT_2,

    /*CLASSIFICATION ALGORITHMS*/
    CLASSIFICATION1,

    /*CLUSTERING ALGORITHMS*/
    CLUSTERING1,
    CLUSTERING2
}
