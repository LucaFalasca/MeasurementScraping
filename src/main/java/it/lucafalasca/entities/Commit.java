package it.lucafalasca.entities;

import java.util.List;

public class Commit {
    private String url;
    private CommitDescr commit;

    public String getUrl() {
        return url;
    }


    public void setUrl(String value) {
        this.url = value;
    }

    public CommitDescr getCommitDescr() {
        return commit;
    }

    public void setCommitDescr(CommitDescr value) {
        this.commit = value;
    }

    @Override
    public String toString() {
        return "Commit{" +
                "url='" + url + '\'' +
                ", commit=" + commit +
                '}';
    }
}
