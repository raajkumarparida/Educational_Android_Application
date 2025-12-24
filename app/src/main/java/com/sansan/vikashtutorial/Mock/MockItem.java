package com.sansan.vikashtutorial.Mock;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.List;

public class MockItem implements Parcelable {
    private String id;
    private String title;
    private String subtitle;
    private String time;
    private String img;
    private List<QuestionModel> questionList;

    public MockItem() {
        this("", "", "", "", "", new ArrayList<>());
    }

    public MockItem(String id, String title, String subtitle, String time, String img, List<QuestionModel> questionList) {
        this.id = id;
        this.title = title;
        this.subtitle = subtitle;
        this.time = time;
        this.img = img;
        this.questionList = questionList;
    }

    protected MockItem(Parcel in) {
        id = in.readString();
        title = in.readString();
        subtitle = in.readString();
        time = in.readString();
        img = in.readString();
        questionList = in.createTypedArrayList(QuestionModel.CREATOR);
    }

    public static final Creator<MockItem> CREATOR = new Creator<MockItem>() {
        @Override
        public MockItem createFromParcel(Parcel in) {
            return new MockItem(in);
        }

        @Override
        public MockItem[] newArray(int size) {
            return new MockItem[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(title);
        dest.writeString(subtitle);
        dest.writeString(time);
        dest.writeString(img);
        dest.writeTypedList(questionList);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getImg() {
        return img;
    }

    public void setImg(String img) {
        this.img = img;
    }

    public List<QuestionModel> getQuestionList() {
        return questionList;
    }

    public void setQuestionList(List<QuestionModel> questionList) {
        this.questionList = questionList;
    }

    public static class QuestionModel implements Parcelable {
        private String question;
        private List<String> options;
        private String correct;
        private boolean isBookmarked;
        private String userAnswer;

        public QuestionModel() {
            this("", new ArrayList<>(), "", false, "");
        }

        public QuestionModel(String question, List<String> options, String correct, Boolean isBookmarked, String userAnswer) {
            this.question = question;
            this.options = options;
            this.correct = correct;
            this.isBookmarked = isBookmarked;
            this.userAnswer = userAnswer;
        }

        protected QuestionModel(Parcel in) {
            question = in.readString();
            options = in.createStringArrayList();
            correct = in.readString();
            isBookmarked = in.readByte() != 0;
            userAnswer = in.readString();
        }

        public static final Creator<QuestionModel> CREATOR = new Creator<QuestionModel>() {
            @Override
            public QuestionModel createFromParcel(Parcel in) {
                return new QuestionModel(in);
            }

            @Override
            public QuestionModel[] newArray(int size) {
                return new QuestionModel[size];
            }
        };

        @Override
        public int describeContents() {
            return 0;
        }

        @Override
        public void writeToParcel(Parcel dest, int flags) {
            dest.writeString(question);
            dest.writeStringList(options);
            dest.writeString(correct);
            dest.writeByte((byte) (isBookmarked ? 1 : 0));
            dest.writeString(userAnswer);
        }
        public String getQuestion() {
            return question;
        }

        public void setQuestion(String question) {
            this.question = question;
        }

        public List<String> getOptions() {
            return options;
        }

        public void setOptions(List<String> options) {
            this.options = options;
        }

        public String getCorrect() {
            return correct;
        }

        public void setCorrect(String correct) {
            this.correct = correct;
        }

        public boolean isBookmarked() {
            return isBookmarked;
        }

        public void setBookmarked(boolean bookmarked) {
            isBookmarked = bookmarked;
        }

        public String getUserAnswer() {
            return userAnswer;
        }

        public void setUserAnswer(String userAnswer) {
            this.userAnswer = userAnswer;
        }
    }
}