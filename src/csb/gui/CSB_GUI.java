package csb.gui;

import static csb.CSB_StartupConstants.*;
import csb.CSB_PropertyType;
import csb.controller.CourseEditController;
import csb.data.Course;
import csb.data.CourseDataManager;
import csb.data.CourseDataView;
import csb.data.CoursePage;
import csb.controller.FileController;
import csb.data.Instructor;
import csb.data.Semester;
import csb.data.Subject;
import csb.file.CourseFileManager;
import csb.file.CourseSiteExporter;
import java.io.IOException;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Rectangle2D;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.Label;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.Screen;
import javafx.stage.Stage;
import properties_manager.PropertiesManager;

/**
 * This class provides the Graphical User Interface for this application,
 * managing all the UI components for editing a Course and exporting it to a
 * site.
 *
 * @author Richard McKenna
 */
public class CSB_GUI implements CourseDataView {

    // THESE CONSTANTS ARE FOR TYING THE PRESENTATION STYLE OF
    // THIS GUI'S COMPONENTS TO A STYLE SHEET THAT IT USES

    static final String PRIMARY_STYLE_SHEET = PATH_CSS + "csb_style.css";
    static final String CLASS_BORDERED_PANE = "bordered_pane";
    static final String CLASS_SUBJECT_PANE = "subject_pane";
    static final String CLASS_HEADING_LABEL = "heading_label";
    static final String CLASS_SUBHEADING_LABEL = "subheading_label";
    static final String CLASS_PROMPT_LABEL = "prompt_label";
    static final String EMPTY_TEXT = "";
    static final int LARGE_TEXT_FIELD_LENGTH = 20;
    static final int SMALL_TEXT_FIELD_LENGTH = 5;

    // THIS MANAGES ALL OF THE APPLICATION'S DATA
    CourseDataManager dataManager;

    // THIS MANAGES COURSE FILE I/O
    CourseFileManager courseFileManager;

    // THIS MANAGES EXPORTING OUR SITE PAGES
    CourseSiteExporter siteExporter;

    // THIS HANDLES INTERACTIONS WITH FILE-RELATED CONTROLS
    FileController fileController;

    // THIS HANDLES INTERACTIONS WITH COURSE INFO CONTROLS
    CourseEditController courseController;

    // THIS IS THE APPLICATION WINDOW
    Stage primaryStage;

    // THIS IS THE STAGE'S SCENE GRAPH
    Scene primaryScene;

    // THIS PANE ORGANIZES THE BIG PICTURE CONTAINERS FOR THE
    // APPLICATION GUI
    BorderPane csbPane;

    // THIS IS THE TOP TOOLBAR AND ITS CONTROLS
    FlowPane fileToolbarPane;
    Button newCourseButton;
    Button saveCourseButton;
    Button exportSiteButton;
    Button exitButton;
    
    //Created new button for loading
    Button loadCourseButton;

    // WE'LL ORGANIZE OUR WORKSPACE COMPONENTS USING A BORDER PANE
    BorderPane workspacePane;
    boolean workspaceActivated;

    // WE'LL PUT THIS IN THE TOP OF THE WORKSPACE, IT WILL
    // HOLD TWO OTHER PANES FULL OF CONTROLS AS WELL AS A LABEL
    VBox topWorkspacePane;
    Label courseHeadingLabel;
    SplitPane topWorkspaceSplitPane;

    // THESE ARE THE CONTROLS FOR THE BASIC SCHEDULE PAGE HEADER INFO
    GridPane courseInfoPane;
    Label courseInfoLabel;
    Label courseSubjectLabel;
    ComboBox courseSubjectComboBox;
    Label courseNumberLabel;
    TextField courseNumberTextField;
    Label courseTitleLabel;
    TextField courseTitleTextField;
    Label instructorNameLabel;
    TextField instructorNameTextField;
    Label instructorURLLabel;
    TextField instructorURLTextField;
    
    Label semesterLabel;
    Label yearLabel;

    // THESE ARE THE CONTROLS FOR SELECTING WHICH PAGES THE SCHEDULE
    // PAGE WILL HAVE TO LINK TO
    VBox pagesSelectionPane;
    Label pagesSelectionLabel;
    CheckBox indexPageCheckBox;
    CheckBox syllabusPageCheckBox;
    CheckBox schedulePageCheckBox;
    CheckBox hwsPageCheckBox;
    CheckBox projectsPageCheckBox;

    // SCHEDULE CONTROLS
    VBox schedulePane;
    VBox scheduleInfoPane;
    Label scheduleInfoHeadingLabel;
    SplitPane splitScheduleInfoPane;

    // THESE GUYS GO IN THE LEFT HALF OF THE splitScheduleInfoPane
    GridPane dateBoundariesPane;
    Label dateBoundariesLabel;
    Label startDateLabel;
    DatePicker startDatePicker;
    Label endDateLabel;
    DatePicker endDatePicker;

    // THESE GUYS GO IN THE RIGHT HALF OF THE splitScheduleInfoPane
    VBox lectureDaySelectorPane;
    Label lectureDaySelectLabel;
    CheckBox mondayCheckBox;
    CheckBox tuesdayCheckBox;
    CheckBox wednesdayCheckBox;
    CheckBox thursdayCheckBox;
    CheckBox fridayCheckBox;
    
    ComboBox semesterComboBox;
    ComboBox yearComboBox;

    /**
     * Constructor for making this GUI, note that it does not initialize the UI
     * controls. To do that, call initGUI.
     *
     * @param initPrimaryStage Window inside which the GUI will be displayed.
     */
    public CSB_GUI(Stage initPrimaryStage) {
        primaryStage = initPrimaryStage;
    }

    /**
     * Accessor method for the data manager.
     *
     * @return The CourseDataManager used by this UI.
     */
    public CourseDataManager getDataManager() {
        return dataManager;
    }

    /**
     * Accessor method for the file controller.
     *
     * @return The FileController used by this UI.
     */
    public FileController getFileController() {
        return fileController;
    }

    /**
     * Accessor method for the course file manager.
     *
     * @return The CourseFileManager used by this UI.
     */
    public CourseFileManager getCourseFileManager() {
        return courseFileManager;
    }

    /**
     * Accessor method for the site exporter.
     *
     * @return The CourseSiteExporter used by this UI.
     */
    public CourseSiteExporter getSiteExporter() {
        return siteExporter;
    }

    /**
     * Accessor method for the window (i.e. stage).
     *
     * @return The window (i.e. Stage) used by this UI.
     */
    public Stage getWindow() {
        return primaryStage;
    }

    /**
     * Mutator method for the data manager.
     *
     * @param initDataManager The CourseDataManager to be used by this UI.
     */
    public void setDataManager(CourseDataManager initDataManager) {
        dataManager = initDataManager;
    }

    /**
     * Mutator method for the course file manager.
     *
     * @param initCourseFileManager The CourseFileManager to be used by this UI.
     */
    public void setCourseFileManager(CourseFileManager initCourseFileManager) {
        courseFileManager = initCourseFileManager;
    }

    /**
     * Mutator method for the site exporter.
     *
     * @param initSiteExporter The CourseSiteExporter to be used by this UI.
     */
    public void setSiteExporter(CourseSiteExporter initSiteExporter) {
        siteExporter = initSiteExporter;
    }

    /**
     * This method fully initializes the user interface for use.
     *
     * @param windowTitle The text to appear in the UI window's title bar.
     * @param subjects The list of subjects to choose from.
     * @throws IOException Thrown if any initialization files fail to load.
     */
    public void initGUI(String windowTitle, ArrayList<String> subjects) throws IOException {
        // INIT THE TOOLBAR
        initFileToolbar();

        // INIT THE CENTER WORKSPACE CONTROLS BUT DON'T ADD THEM
        // TO THE WINDOW YET
        initWorkspace(subjects);

        // NOW SETUP THE EVENT HANDLERS
        initEventHandlers();

        // AND FINALLY START UP THE WINDOW (WITHOUT THE WORKSPACE)
        initWindow(windowTitle);
    }

    /**
     * When called this function puts the workspace into the window,
     * revealing the controls for editing a Course.
     */
    public void activateWorkspace() {
        if (!workspaceActivated) {
            // PUT THE WORKSPACE IN THE GUI
            csbPane.setCenter(workspacePane);
            workspaceActivated = true;
        }
    }
    
    /**
     * This function takes all of the data out of the courseToReload 
     * argument and loads its values into the user interface controls.
     * 
     * @param courseToReload The Course whose data we'll load into the GUI.
     */
    @Override
    public void reloadCourse(Course courseToReload) {
        // FIRST ACTIVATE THE WORKSPACE IF NECESSARY
        if (!workspaceActivated) {
            activateWorkspace();
        }

        // WE DON'T WANT TO RESPOND TO EVENTS FORCED BY
        // OUR INITIALIZATION SELECTIONS
        courseController.enable(false);

        // FIRST LOAD ALL THE BASIC COURSE INFO
        courseSubjectComboBox.setValue(courseToReload.getSubject());
        courseNumberTextField.setText("" + courseToReload.getNumber());
        courseTitleTextField.setText(courseToReload.getTitle());
        instructorNameTextField.setText(courseToReload.getInstructor().getName());
        instructorURLTextField.setText(courseToReload.getInstructor().getHomepageURL());
        indexPageCheckBox.setSelected(courseToReload.hasCoursePage(CoursePage.INDEX));
        syllabusPageCheckBox.setSelected(courseToReload.hasCoursePage(CoursePage.SYLLABUS));
        schedulePageCheckBox.setSelected(courseToReload.hasCoursePage(CoursePage.SCHEDULE));
        hwsPageCheckBox.setSelected(courseToReload.hasCoursePage(CoursePage.HWS));
        projectsPageCheckBox.setSelected(courseToReload.hasCoursePage(CoursePage.PROJECTS));
                
        //Sets values for semester and year combo box
        semesterComboBox.setValue(courseToReload.getSemester());
        yearComboBox.setValue(courseToReload.getYear());
        
       

        // THEN THE DATE PICKERS
        LocalDate startDate = courseToReload.getStartingMonday();
        startDatePicker.setValue(startDate);
        LocalDate endDate = courseToReload.getEndingFriday();
        endDatePicker.setValue(endDate);

        // AND THE LECTURE DAY CHECK BOXES
        mondayCheckBox.setSelected(courseToReload.hasLectureDay(DayOfWeek.MONDAY));
        tuesdayCheckBox.setSelected(courseToReload.hasLectureDay(DayOfWeek.TUESDAY));
        wednesdayCheckBox.setSelected(courseToReload.hasLectureDay(DayOfWeek.WEDNESDAY));
        thursdayCheckBox.setSelected(courseToReload.hasLectureDay(DayOfWeek.THURSDAY));
        fridayCheckBox.setSelected(courseToReload.hasLectureDay(DayOfWeek.FRIDAY));

        // NOW WE DO WANT TO RESPOND WHEN THE USER INTERACTS WITH OUR CONTROLS
        courseController.enable(true);
    }

    /**
     * This method is used to activate/deactivate toolbar buttons when
     * they can and cannot be used so as to provide foolproof design.
     * 
     * @param saved Describes whether the loaded Course has been saved or not.
     */
    public void updateToolbarControls(boolean saved) {
        // THIS TOGGLES WITH WHETHER THE CURRENT COURSE
        // HAS BEEN SAVED OR NOT
        saveCourseButton.setDisable(saved);

        // ALL THE OTHER BUTTONS ARE ALWAYS ENABLED
        // ONCE EDITING THAT FIRST COURSE BEGINS
        exportSiteButton.setDisable(false);

        // NOTE THAT THE NEW, LOAD, AND EXIT BUTTONS
        // ARE NEVER DISABLED SO WE NEVER HAVE TO TOUCH THEM
        
        //Activates load course button
        loadCourseButton.setDisable(saved);
        
    }

    /**
     * This function loads all the values currently in the user interface
     * into the course argument.
     * 
     * @param course The course to be updated using the data from the UI controls.
     */
    public void updateCourseInfo(Course course) {
        course.setSubject(Subject.valueOf(courseSubjectComboBox.getSelectionModel().getSelectedItem().toString()));
        course.setNumber(Integer.parseInt(courseNumberTextField.getText()));
        course.setTitle(courseTitleTextField.getText());
        Instructor instructor = course.getInstructor();
        instructor.setName(instructorNameTextField.getText());
        instructor.setHomepageURL(instructorURLTextField.getText());
        updatePageUsingCheckBox(indexPageCheckBox, course, CoursePage.INDEX);
        updatePageUsingCheckBox(syllabusPageCheckBox, course, CoursePage.SYLLABUS);
        updatePageUsingCheckBox(schedulePageCheckBox, course, CoursePage.SCHEDULE);
        updatePageUsingCheckBox(hwsPageCheckBox, course, CoursePage.HWS);
        updatePageUsingCheckBox(projectsPageCheckBox, course, CoursePage.PROJECTS);
        course.setStartingMonday(startDatePicker.getValue());
        course.setEndingFriday(endDatePicker.getValue());
        course.selectLectureDay(DayOfWeek.MONDAY, mondayCheckBox.isSelected());
        course.selectLectureDay(DayOfWeek.TUESDAY, tuesdayCheckBox.isSelected());
        course.selectLectureDay(DayOfWeek.WEDNESDAY, wednesdayCheckBox.isSelected());
        course.selectLectureDay(DayOfWeek.THURSDAY, thursdayCheckBox.isSelected());
        course.selectLectureDay(DayOfWeek.FRIDAY, fridayCheckBox.isSelected());
        
        //Reads the gui and loads the year and semester
        course.setYear(Integer.parseInt(yearComboBox.getSelectionModel().getSelectedItem().toString()));
        course.setSemester(semesterComboBox.getSelectionModel().getSelectedItem().toString());
        
    }

    /****************************************************************************/
    /* BELOW ARE ALL THE PRIVATE HELPER METHODS WE USE FOR INITIALIZING OUR GUI */
    /****************************************************************************/
    
    /**
     * This function initializes all the buttons in the toolbar at the top of
     * the application window. These are related to file management.
     */
    private void initFileToolbar() {
        fileToolbarPane = new FlowPane();

        // HERE ARE OUR FILE TOOLBAR BUTTONS, NOTE THAT SOME WILL
        // START AS ENABLED (false), WHILE OTHERS DISABLED (true)
        //Added load button to the GUI
        newCourseButton = initChildButton(fileToolbarPane, CSB_PropertyType.NEW_COURSE_ICON, CSB_PropertyType.NEW_COURSE_TOOLTIP, false);
        loadCourseButton = initChildButton(fileToolbarPane, CSB_PropertyType.LOAD_COURSE_ICON, CSB_PropertyType.LOAD_COURSE_TOOLTIP, true);
        saveCourseButton = initChildButton(fileToolbarPane, CSB_PropertyType.SAVE_COURSE_ICON, CSB_PropertyType.SAVE_COURSE_TOOLTIP, true);
        exportSiteButton = initChildButton(fileToolbarPane, CSB_PropertyType.EXPORT_PAGE_ICON, CSB_PropertyType.EXPORT_PAGE_TOOLTIP, true);
        exitButton = initChildButton(fileToolbarPane, CSB_PropertyType.EXIT_ICON, CSB_PropertyType.EXIT_TOOLTIP, false);
        
        
    }

    // CREATES AND SETS UP ALL THE CONTROLS TO GO IN THE APP WORKSPACE
    private void initWorkspace(ArrayList<String> subjects) throws IOException {
        // THE WORKSPACE HAS A FEW REGIONS, THIS 
        // IS FOR BASIC COURSE EDITING CONTROLS
        initBasicCourseInfoControls(subjects);

        // THIS IS FOR SELECTING PAGE LINKS TO INCLUDE
        initPageSelectionControls();

        // THE TOP WORKSPACE HOLDS BOTH THE BASIC COURSE INFO
        // CONTROLS AS WELL AS THE PAGE SELECTION CONTROLS
        initTopWorkspace();

        // THIS IS FOR MANAGING SCHEDULE EDITING
        initScheduleItemsControls();

        // THIS HOLDS ALL OUR WORKSPACE COMPONENTS, SO NOW WE MUST
        // ADD THE COMPONENTS WE'VE JUST INITIALIZED
        workspacePane = new BorderPane();
        workspacePane.setTop(topWorkspacePane);
        workspacePane.setCenter(schedulePane);
        workspacePane.getStyleClass().add(CLASS_BORDERED_PANE);

        // NOTE THAT WE HAVE NOT PUT THE WORKSPACE INTO THE WINDOW,
        // THAT WILL BE DONE WHEN THE USER EITHER CREATES A NEW
        // COURSE OR LOADS AN EXISTING ONE FOR EDITING
        workspaceActivated = false;
    }
    
    // INITIALIZES THE TOP PORTION OF THE WORKWPACE UI
    private void initTopWorkspace() {
        // HERE'S THE SPLIT PANE, ADD THE TWO GROUPS OF CONTROLS
        topWorkspaceSplitPane = new SplitPane();
        topWorkspaceSplitPane.getItems().add(courseInfoPane);
        topWorkspaceSplitPane.getItems().add(pagesSelectionPane);

        // THE TOP WORKSPACE PANE WILL ONLY DIRECTLY HOLD 2 THINGS, A LABEL
        // AND A SPLIT PANE, WHICH WILL HOLD 2 ADDITIONAL GROUPS OF CONTROLS
        topWorkspacePane = new VBox();
        topWorkspacePane.getStyleClass().add(CLASS_BORDERED_PANE);

        // HERE'S THE LABEL
        courseHeadingLabel = initChildLabel(topWorkspacePane, CSB_PropertyType.COURSE_HEADING_LABEL, CLASS_HEADING_LABEL);

        // AND NOW ADD THE SPLIT PANE
        topWorkspacePane.getChildren().add(topWorkspaceSplitPane);
    }

    // INITIALIZES THE CONTROLS IN THE LEFT HALF OF THE TOP WORKSPACE
    private void initBasicCourseInfoControls(ArrayList<String> subjects) throws IOException {
        // THESE ARE THE CONTROLS FOR THE BASIC SCHEDULE PAGE HEADER INFO
        // WE'LL ARRANGE THEM IN THE LEFT SIDE IN A VBox
        courseInfoPane = new GridPane();

        // FIRST THE HEADING LABEL
        courseInfoLabel = initGridLabel(courseInfoPane, CSB_PropertyType.COURSE_INFO_LABEL, CLASS_SUBHEADING_LABEL, 0, 0, 4, 1);

        // THEN CONTROLS FOR CHOOSING THE SUBJECT
        courseSubjectLabel = initGridLabel(courseInfoPane, CSB_PropertyType.COURSE_SUBJECT_LABEL, CLASS_PROMPT_LABEL, 0, 1, 1, 1);
        courseSubjectComboBox = initGridComboBox(courseInfoPane, 1, 1, 1, 1);
        loadSubjectComboBox(subjects);

        // THEN CONTROLS FOR UPDATING THE COURSE NUMBER
        courseNumberLabel = initGridLabel(courseInfoPane, CSB_PropertyType.COURSE_NUMBER_LABEL, CLASS_PROMPT_LABEL, 2, 1, 1, 1);
        courseNumberTextField = initGridTextField(courseInfoPane, SMALL_TEXT_FIELD_LENGTH, EMPTY_TEXT, true, 3, 1, 1, 1);

        // THEN THE COURSE TITLE
        courseTitleLabel = initGridLabel(courseInfoPane, CSB_PropertyType.COURSE_TITLE_LABEL, CLASS_PROMPT_LABEL, 0, 3, 1, 1);
        courseTitleTextField = initGridTextField(courseInfoPane, LARGE_TEXT_FIELD_LENGTH, EMPTY_TEXT, true, 1, 3, 3, 1);

        // THEN THE INSTRUCTOR NAME
        instructorNameLabel = initGridLabel(courseInfoPane, CSB_PropertyType.INSTRUCTOR_NAME_LABEL, CLASS_PROMPT_LABEL, 0, 4, 1, 1);
        instructorNameTextField = initGridTextField(courseInfoPane, LARGE_TEXT_FIELD_LENGTH, EMPTY_TEXT, true, 1, 4, 3, 1);

        // AND THE INSTRUCTOR HOMEPAGE
        instructorURLLabel = initGridLabel(courseInfoPane, CSB_PropertyType.INSTRUCTOR_URL_LABEL, CLASS_PROMPT_LABEL, 0, 5, 1, 1);
        instructorURLTextField = initGridTextField(courseInfoPane, LARGE_TEXT_FIELD_LENGTH, EMPTY_TEXT, true, 1, 5, 3, 1);
        
        
        
        
        //Put semester and year labels. Fills semester and year combo boxes as well 
        semesterLabel = initGridLabel(courseInfoPane, CSB_PropertyType.SEMESTER_LABEL, CLASS_PROMPT_LABEL, 0, 2, 1, 1);
        semesterComboBox = initGridComboBox(courseInfoPane, 1, 2, 1, 1);
        semesterComboBox.getItems().addAll("Fall", "Spring", "Winter", "Summer 1", "Summer 2", "Summer EXT");
       
        yearLabel = initGridLabel(courseInfoPane, CSB_PropertyType.YEAR_LABEL, CLASS_PROMPT_LABEL, 2, 2, 1, 1);
        yearComboBox = initGridComboBox(courseInfoPane, 3, 2, 1, 1);
        yearComboBox.getItems().add(Calendar.getInstance().get(Calendar.YEAR));
        yearComboBox.getItems().add(Calendar.getInstance().get(Calendar.YEAR)+1);
    }

    // INITIALIZES THE CONTROLS IN THE RIGHT HALF OF THE TOP WORKSPACE
    private void initPageSelectionControls() {
        // THESE ARE THE CONTROLS FOR SELECTING WHICH PAGES THE SCHEDULE
        // PAGE WILL HAVE TO LINK TO
        pagesSelectionPane = new VBox();
        pagesSelectionPane.getStyleClass().add(CLASS_SUBJECT_PANE);
        pagesSelectionLabel = initChildLabel(pagesSelectionPane, CSB_PropertyType.PAGES_SELECTION_HEADING_LABEL, CLASS_SUBHEADING_LABEL);
        indexPageCheckBox = initChildCheckBox(pagesSelectionPane, CourseSiteExporter.INDEX_PAGE);
        syllabusPageCheckBox = initChildCheckBox(pagesSelectionPane, CourseSiteExporter.SYLLABUS_PAGE);
        schedulePageCheckBox = initChildCheckBox(pagesSelectionPane, CourseSiteExporter.SCHEDULE_PAGE);
        hwsPageCheckBox = initChildCheckBox(pagesSelectionPane, CourseSiteExporter.HWS_PAGE);
        projectsPageCheckBox = initChildCheckBox(pagesSelectionPane, CourseSiteExporter.PROJECTS_PAGE);
    }
    
    // INITIALIZE THE SCHEDULE ITEMS CONTROLS
    private void initScheduleItemsControls() {
        // FOR THE LEFT
        dateBoundariesPane = new GridPane();
        dateBoundariesLabel = initGridLabel(dateBoundariesPane, CSB_PropertyType.DATE_BOUNDARIES_LABEL, CLASS_SUBHEADING_LABEL, 0, 0, 1, 1);
        startDateLabel = initGridLabel(dateBoundariesPane, CSB_PropertyType.STARTING_MONDAY_LABEL, CLASS_PROMPT_LABEL, 0, 1, 1, 1);
        startDatePicker = initGridDatePicker(dateBoundariesPane, 1, 1, 1, 1);
        endDateLabel = initGridLabel(dateBoundariesPane, CSB_PropertyType.ENDING_FRIDAY_LABEL, CLASS_PROMPT_LABEL, 0, 2, 1, 1);
        endDatePicker = initGridDatePicker(dateBoundariesPane, 1, 2, 1, 1);

        // THIS ONE IS ON THE RIGHT
        lectureDaySelectorPane = new VBox();
        lectureDaySelectLabel = initChildLabel(lectureDaySelectorPane, CSB_PropertyType.LECTURE_DAY_SELECT_LABEL, CLASS_SUBHEADING_LABEL);
        mondayCheckBox = initChildCheckBox(lectureDaySelectorPane, CourseSiteExporter.MONDAY_HEADER);
        tuesdayCheckBox = initChildCheckBox(lectureDaySelectorPane, CourseSiteExporter.TUESDAY_HEADER);
        wednesdayCheckBox = initChildCheckBox(lectureDaySelectorPane, CourseSiteExporter.WEDNESDAY_HEADER);
        thursdayCheckBox = initChildCheckBox(lectureDaySelectorPane, CourseSiteExporter.THURSDAY_HEADER);
        fridayCheckBox = initChildCheckBox(lectureDaySelectorPane, CourseSiteExporter.FRIDAY_HEADER);

        // THIS SPLITS THE TOP
        splitScheduleInfoPane = new SplitPane();
        splitScheduleInfoPane.getItems().add(dateBoundariesPane);
        splitScheduleInfoPane.getItems().add(lectureDaySelectorPane);

        // THIS IS FOR STUFF IN THE TOP OF THE SCHEDULE PANE, WE NEED TO PUT TWO THINGS INSIDE
        scheduleInfoPane = new VBox();

        // FIRST OUR SCHEDULE HEADER
        scheduleInfoHeadingLabel = initChildLabel(scheduleInfoPane, CSB_PropertyType.SCHEDULE_HEADING_LABEL, CLASS_HEADING_LABEL);

        // AND THEN THE SPLIT PANE
        scheduleInfoPane.getChildren().add(splitScheduleInfoPane);

        // FINALLY, EVERYTHING IN THIS REGION ULTIMATELY GOES INTO schedulePane
        schedulePane = new VBox();
        schedulePane.getChildren().add(scheduleInfoPane);
        schedulePane.getStyleClass().add(CLASS_BORDERED_PANE);
    }

    // INITIALIZE THE WINDOW (i.e. STAGE) PUTTING ALL THE CONTROLS
    // THERE EXCEPT THE WORKSPACE, WHICH WILL BE ADDED THE FIRST
    // TIME A NEW Course IS CREATED OR LOADED
    private void initWindow(String windowTitle) {
        // SET THE WINDOW TITLE
        primaryStage.setTitle(windowTitle);

        // GET THE SIZE OF THE SCREEN
        Screen screen = Screen.getPrimary();
        Rectangle2D bounds = screen.getVisualBounds();

        // AND USE IT TO SIZE THE WINDOW
        primaryStage.setX(bounds.getMinX());
        primaryStage.setY(bounds.getMinY());
        primaryStage.setWidth(bounds.getWidth());
        primaryStage.setHeight(bounds.getHeight());

        // ADD THE TOOLBAR ONLY, NOTE THAT THE WORKSPACE
        // HAS BEEN CONSTRUCTED, BUT WON'T BE ADDED UNTIL
        // THE USER STARTS EDITING A COURSE
        csbPane = new BorderPane();
        csbPane.setTop(fileToolbarPane);
        primaryScene = new Scene(csbPane);

        // NOW TIE THE SCENE TO THE WINDOW, SELECT THE STYLESHEET
        // WE'LL USE TO STYLIZE OUR GUI CONTROLS, AND OPEN THE WINDOW
        primaryScene.getStylesheets().add(PRIMARY_STYLE_SHEET);
        primaryStage.setScene(primaryScene);
        primaryStage.show();
    }

    // INIT ALL THE EVENT HANDLERS
    private void initEventHandlers() throws IOException {
        // FIRST THE FILE CONTROLS
        fileController = new FileController(primaryStage, courseFileManager, siteExporter);
        newCourseButton.setOnAction(e -> {
            fileController.handleNewCourseRequest(this);
        });
        saveCourseButton.setOnAction(e -> {
            fileController.handleSaveCourseRequest(this, dataManager.getCourse());
        });
  
        //Sets action listener to load button 
        loadCourseButton.setOnAction(e -> {
            fileController.handleLoadCourseRequest(this, dataManager.getCourse());
        });
        
  
        exportSiteButton.setOnAction(e -> {
            fileController.handleExportCourseRequest(this);
        });
        exitButton.setOnAction(e -> {
            fileController.handleExitRequest(this);
        });

        // THEN THE COURSE EDITING CONTROLS
        courseController = new CourseEditController();
        courseSubjectComboBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        
        
        //Added action listeners for semestercombobox and yearcombobox
        semesterComboBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        
        yearComboBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });

        
        indexPageCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        syllabusPageCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        schedulePageCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        hwsPageCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        projectsPageCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });

        // TEXT FIELDS HAVE A DIFFERENT WAY OF LISTENING FOR TEXT CHANGES
        registerTextFieldController(courseNumberTextField);
        registerTextFieldController(courseTitleTextField);
        registerTextFieldController(instructorNameTextField);
        registerTextFieldController(instructorURLTextField);

        // THE DATE SELECTION ONES HAVE PARTICULAR CONCERNS, AND SO
        // GO THROUGH A DIFFERENT METHOD
        startDatePicker.setOnAction(e -> {
            courseController.handleDateSelectionRequest(this, startDatePicker, endDatePicker);
        });
        endDatePicker.setOnAction(e -> {
            courseController.handleDateSelectionRequest(this, startDatePicker, endDatePicker);
        });

        // AND THE LECTURE DAYS CHECKBOXES
        mondayCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        tuesdayCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        wednesdayCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        thursdayCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
        fridayCheckBox.setOnAction(e -> {
            courseController.handleCourseChangeRequest(this);
        });
    }

    // REGISTER THE EVENT LISTENER FOR A TEXT FIELD
    private void registerTextFieldController(TextField textField) {
        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            courseController.handleCourseChangeRequest(this);
        });
    }
    // INIT A BUTTON AND ADD IT TO A CONTAINER IN A TOOLBAR
    private Button initChildButton(Pane toolbar, CSB_PropertyType icon, CSB_PropertyType tooltip, boolean disabled) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String imagePath = "file:" + PATH_IMAGES + props.getProperty(icon.toString());
        Image buttonImage = new Image(imagePath);
        Button button = new Button();
        button.setDisable(disabled);
        button.setGraphic(new ImageView(buttonImage));
        Tooltip buttonTooltip = new Tooltip(props.getProperty(tooltip.toString()));
        button.setTooltip(buttonTooltip);
        toolbar.getChildren().add(button);
        return button;
    }
    
    // INIT A LABEL AND SET IT'S STYLESHEET CLASS
    private Label initLabel(CSB_PropertyType labelProperty, String styleClass) {
        PropertiesManager props = PropertiesManager.getPropertiesManager();
        String labelText = props.getProperty(labelProperty);
        Label label = new Label(labelText);
        label.getStyleClass().add(styleClass);
        return label;
    }

    // INIT A LABEL AND PLACE IT IN A GridPane INIT ITS PROPER PLACE
    private Label initGridLabel(GridPane container, CSB_PropertyType labelProperty, String styleClass, int col, int row, int colSpan, int rowSpan) {
        Label label = initLabel(labelProperty, styleClass);
        container.add(label, col, row, colSpan, rowSpan);
        return label;
    }

    // INIT A LABEL AND PUT IT IN A TOOLBAR
    private Label initChildLabel(Pane container, CSB_PropertyType labelProperty, String styleClass) {
        Label label = initLabel(labelProperty, styleClass);
        container.getChildren().add(label);
        return label;
    }

    // INIT A COMBO BOX AND PUT IT IN A GridPane
    private ComboBox initGridComboBox(GridPane container, int col, int row, int colSpan, int rowSpan) throws IOException {
        ComboBox comboBox = new ComboBox();
        container.add(comboBox, col, row, colSpan, rowSpan);
        return comboBox;
    }

    // LOAD THE COMBO BOX TO HOLD Course SUBJECTS
    private void loadSubjectComboBox(ArrayList<String> subjects) {
        for (String s : subjects) {
            //Cuts the quotations off the subjects
            courseSubjectComboBox.getItems().add(s.substring(1, 4));
        }
    }

    // INIT A TEXT FIELD AND PUT IT IN A GridPane
    private TextField initGridTextField(GridPane container, int size, String initText, boolean editable, int col, int row, int colSpan, int rowSpan) {
        TextField tf = new TextField();
        tf.setPrefColumnCount(size);
        tf.setText(initText);
        tf.setEditable(editable);
        container.add(tf, col, row, colSpan, rowSpan);
        return tf;
    }

    // INIT A DatePicker AND PUT IT IN A GridPane
    private DatePicker initGridDatePicker(GridPane container, int col, int row, int colSpan, int rowSpan) {
        DatePicker datePicker = new DatePicker();
        container.add(datePicker, col, row, colSpan, rowSpan);
        return datePicker;
    }

    // INIT A CheckBox AND PUT IT IN A TOOLBAR
    private CheckBox initChildCheckBox(Pane container, String text) {
        CheckBox cB = new CheckBox(text);
        container.getChildren().add(cB);
        return cB;
    }

    // INIT A DatePicker AND PUT IT IN A CONTAINER
    private DatePicker initChildDatePicker(Pane container) {
        DatePicker dp = new DatePicker();
        container.getChildren().add(dp);
        return dp;
    }
    
    // LOADS CHECKBOX DATA INTO A Course OBJECT REPRESENTING A CoursePage
    private void updatePageUsingCheckBox(CheckBox cB, Course course, CoursePage cP) {
        if (cB.isSelected()) {
            course.selectPage(cP);
        } else {
            course.unselectPage(cP);
        }
    }    
}
